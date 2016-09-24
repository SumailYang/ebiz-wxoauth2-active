package com.saic.framework.web.wechat.interceptor.verifcationhandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.interceptor.verifcationhandler.SignonVerifcationHandler;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.framework.web.wechat.util.WechatUtil;
import com.saic.sso.client.SSOClient;
import com.saic.sso.client.SSOUtil;

public class WeChatSignVerifHandle implements SignonVerifcationHandler {
	
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(WeChatSignVerifHandle.class);
	
	private final static String cxhwxFlag ="cxhwx";
	@Autowired
	private UserLoginService userLoginService;
	
	 @Autowired(required=false)
	 private SSOClient ssoClient;
	
	@Override
	public Boolean isMatched(HttpServletRequest request) {
		Boolean flag = FrameworkUtil.isWXBrower(request,WxConstant.isTestEnable());
		if(flag){
		    String wechatResource = request.getParameter(cxhwxFlag);
		    if("false".equalsIgnoreCase(wechatResource)){
		        return false;
		    }
		}
		return flag;
	}

	@Override
	public Long signProcess(HttpServletRequest request,HttpServletResponse response ,
			Object handler) {
//		LOGGER.info("==== WeChatSignVerif.SignProcess start================");
		// 获取session 和登录状态
		//TODO 假设如果当前微信用户已经登录，那么threadLocal中必须有openId 该假设必须成立
		//再从session中取userId
		//如果session中userId 存在，直接return
		//如果session中的userId 不存在，则通过openId 调用mdm 获取userId，并且userId 放到session中
		
		Long userId = WechatUtil.getCurrentUserId();
		LOGGER.info("WeChatSignVerif.SignProcess|userId={}",userId);
		/**
		 * 判断当前userId是否存在和用户登录状态
		 */
		if (userId != null && userId > 0L) {
		    checkUserIdAndWriterCookie(userId, request, response);
			return userId;
		}

		try {
			// 如果同步session没有成功，在判断redict2Login
			//TODO getCurrentUserLoginToken 应该从threadlocal中取openId
			String openId = WechatUtil.getCurrentUserOpenId();
			userId =  userLoginService.getUserIdByOpenId(openId);
			if (userId != null && userId > 0L) {
	            WechatUtil.saveUserIdToSession(userId,request, response);
	            checkUserIdAndWriterCookie(userId, request, response);
	            return userId;
	        }
		} catch (Exception e) {
			LOGGER.error(
					"WeChatSignVerifHandle|synchronization user infomation error [userId:"
							+ userId + "]：", e);
//			FrameworkUtil.writeErrorOut(response, "/page/error_page_user.html");
		}
		return null;
	}

	@Override
	public ApplicationChannel getApplicationChannel() {
		return ApplicationChannel.WECHAT;
	}

	
	@Override
	public void gotoLogin(HttpServletRequest request,HttpServletResponse response,String callBackUrl) {
		//TODO 如果是ajax 返回一个错误代码与url
		//TODO 如果是普通请求，重定向url
	    String url;
	    if(org.apache.commons.lang3.StringUtils.isEmpty(callBackUrl)){
	         url = userLoginService.generateUserLoginUrl(request);
	    }else{
	        url = callBackUrl;
	    }
		LOGGER.info("WeChatSignVerif.gotoLogin|url={}",url);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
		    LOGGER.error("error on send redirect",e);
//			e.printStackTrace();
		}
	}

    @Override
    public String getDefaultCallBackUrl(HttpServletRequest request) {
        return userLoginService.generateUserLoginUrl(request);
    }
    
    
    private void checkUserIdAndWriterCookie(Long curUserId,HttpServletRequest request,HttpServletResponse response){
        //从cookie中获取userId
        String signValue = SSOUtil.getCookieAnalysis(request);
        LOGGER.debug("WeChatSignVerif.checkUserIdAndWriterCookie|get userid from ssoutil，signValue={}",signValue);
        if(StringUtils.isEmpty(signValue) || !curUserId.toString().equals(signValue)){
            String domain = ssoClient.getDomain();
            SSOUtil.writeAnalysisCookie(curUserId.toString(), domain, response);
            LOGGER.debug("WeChatSignVerif.checkUserIdAndWriterCookie|put userid,domain={}",domain);
        }
    }

}
