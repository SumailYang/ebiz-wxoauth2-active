package com.saic.framework.web.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.saic.framework.web.annotation.SignonVerification;
import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.exception.NoMatchedProcessException;
import com.saic.framework.web.interceptor.listener.UserSignData;
import com.saic.framework.web.interceptor.listener.UserSignListener;
import com.saic.framework.web.interceptor.verifcationhandler.SignonVerifcationHandler;
import com.saic.framework.web.wechat.util.DataUtils;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.framework.web.wechat.util.RequestUtil;

/**
 * 用户登录拦截
 * 
 * @author
 *
 */
public class UserLoginInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserLoginInterceptor.class);

    private  List<SignonVerifcationHandler> signVerifHandlerList;
    
    private List<UserSignListener> userSignListenerList;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        LOGGER.info("user login interceptor start, currenturl:" + RequestUtil.getCurrentRequestUrlAddress(request));
        
        /**
         * 判断当前访问uri是否正常，如果是js，image或者是css 直接返回，如果不是，就验证@WXUserInfo annotation是否存在
         */
        if (FrameworkUtil.isStaticResourceFile(request)) {
        	LOGGER.debug("preHandle|isStaticResourceFile is static source");
            return true;
        }
        
        if(signVerifHandlerList == null || signVerifHandlerList.size() ==0){
        	LOGGER.warn("preHandle|signVerifList size= 0");
        	return true;
        }
        
        SignonVerification annotation = null;

        /**
         * 获取当前方法的annotation
         */
        if (handler instanceof HandlerMethod) {
            annotation = FrameworkUtil.findAnnotation((HandlerMethod) handler, SignonVerification.class);
        }
        
        if (annotation == null) {
            LOGGER.debug("userloginInterceptor annotion do not match");
            return true;
        }
        
//        LOGGER.info("preHandle|annotation:" + annotation);
        // clean threadlocal 
        cleanThreadLocal();
        String supportApplicationChannels = annotation.supportApplicationChannels();
        boolean supportChannelFlag = true;
        boolean verifFlag = false;
//        String matchSource = null;
        for (int i = 0; i < signVerifHandlerList.size(); i++) {
        	SignonVerifcationHandler temp = signVerifHandlerList.get(i);
        	String source = temp.getApplicationChannel().toString();
        	LOGGER.info("signon handler ismatched source=" + source);//日志打到matched 里面去
        	if (temp.isMatched(request)) { 
        	    verifFlag =true;
        	    if(!StringUtils.isEmpty(supportApplicationChannels)){
        	       supportChannelFlag = StringUtils.contains(supportApplicationChannels,source);
        	       LOGGER.info("signon handler supportChannelFlag=" + supportChannelFlag);
        	       if(!supportChannelFlag){
//        	           matchSource=source;
        	           break;
        	       }
                }
        	    
        		DataUtils.threadSourceData.set(source);
        		Long userId = temp.signProcess(request,response, handler);
        		LOGGER.info("preHandle|signProcess:userId=" + userId);
        		//增加用户登录广播
        		UserSignData paramData = new UserSignData();
        		paramData.setChannel(source);
        		paramData.setUserId(userId);
        		if (null != userSignListenerList && userSignListenerList.size() > 0) {
					for (UserSignListener userSignListener : userSignListenerList) {
						 userSignListener.execute(paramData);
					}
				}
        		
        		if (userId == null || userId == 0L) {
        			if (annotation.redict2Login()) {
        			    String callbackUrl = "";
        			    HandlerMethod controlHandlerMethod = (HandlerMethod)handler;
        			    if(controlHandlerMethod.getBean() instanceof LoginCallback){
        			        LOGGER.info("signon handler is callback, calss=" + controlHandlerMethod.getBean().getClass());
        			        LoginCallbackData data = new LoginCallbackData();
        			        data.setAppChannel(temp.getApplicationChannel());
        			        data.setDefaultURl(temp.getDefaultCallBackUrl(request));
        			        
        			        Class[] paramClazzArr = new Class[]{LoginCallbackData.class,HttpServletRequest.class,HttpServletResponse.class};
        			        Method method = controlHandlerMethod.getBean().getClass().getMethod("getLoginCallbackUrl", paramClazzArr);
        			        callbackUrl = (String) method.invoke(controlHandlerMethod.getBean(),  new Object[] {data,request,response});
        			        LOGGER.info("signon handler invoke callback, callbackUrl=" + callbackUrl+";defaultURL="+data.getDefaultURl());
        			    }       
        			    LOGGER.debug("preHandle|gotoLogin");
        				temp.gotoLogin(request, response,callbackUrl);
    					return false;
    				}
				}else{
					DataUtils.threadUserIdData.set(userId);
	        		LOGGER.debug("preHandle|threadlocal setvaule,userId=:" + userId+",source="+source);
					return true;
				}
        		//找到了match，但是为强迫登录
        		break;
			}
		}
        
        if(!supportChannelFlag){
            LOGGER.debug("preHandle|supportChannelFlag is false, redirect notSupportChannelpage!");
            String[] channelArr = new String[]{"PC","WECHAT","CXB","WAP","DEFAULT"};
            String[] channelArrZN =new String[]{"PC端游览器","微信客户端","车享宝APP","WAP端游览器","WAP端游览器"};
            for (int i = 0; i < channelArrZN.length; i++) {
                supportApplicationChannels = supportApplicationChannels.replace(channelArr[i], channelArrZN[i]);
            }
            
//            FrameworkUtil.writeErrorOut(response, "/page/error_page_not_support_channel.html?spChannel="+supportApplicationChannels);
            FrameworkUtil.writeErrorOut(response, "/page/error_page_not_support_channel.html",supportApplicationChannels);
            return false;
        }
        
        if(!verifFlag){
        	LOGGER.debug("preHandle|all handler not matched");
        	if (annotation.redict2Login()){
                throw new NoMatchedProcessException("Matching the processing not found!");
        	}
//        	return false;
        }
        return true;
    }
    
    /**
	 * This implementation is empty.
	 */
	public void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		cleanThreadLocal();
		listenerCallback();
	}
	
	private void listenerCallback(){
		if (null != userSignListenerList && userSignListenerList.size() > 0) {
			for (UserSignListener userSignListener : userSignListenerList) {
				 userSignListener.complateCallback();
			}
		}
	}
	
	private void cleanThreadLocal(){
		DataUtils.threadUserIdData.remove();
		DataUtils.threadSourceData.remove();
	}

	public List<SignonVerifcationHandler> getSignVerifList() {
		return signVerifHandlerList;
	}

	public void setSignVerifList(List<SignonVerifcationHandler> signVerifList) {
		this.signVerifHandlerList = signVerifList;
	}

	public List<UserSignListener> getUserSignListenerList() {
		return userSignListenerList;
	}

	public void setUserSignListenerList(List<UserSignListener> userSignListenerList) {
		this.userSignListenerList = userSignListenerList;
	}
	
}