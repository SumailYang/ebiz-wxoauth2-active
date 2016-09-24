package com.saic.framework.web.wechat.interceptor;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.saic.framework.web.wechat.annotation.WXUserInfo;
import com.saic.framework.web.wechat.annotation.WXUserInfoScope;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.exception.WechatAjaxNotSupporttedException;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.DESUtil;
import com.saic.framework.web.wechat.util.DataUtils;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.framework.web.wechat.util.OAuthUtil;
import com.saic.framework.web.wechat.util.RequestUtil;
import com.saic.framework.web.wechat.util.WechatUtil;
import com.saic.sso.client.SSOClient;
import com.saic.sso.client.SSOUtil;
import com.saic.sso.client.constant.User;

/**
 * 
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 * 
 * @author wangjiayan
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class WXUserInfoInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(WXUserInfoInterceptor.class);

    @Autowired
    private UserLoginService userLoginService;
    

    @Deprecated
    private boolean isTest = false;

    @Deprecated
    public void setIsTest(boolean isTest) {
        this.isTest = isTest;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        LOGGER.info("start WXUserInfoInterceptor currenturl:" + RequestUtil.getCurrentRequestUrlAddress(request));

        // 添加注解判断
        /**
         * 1.判断当前访问url对应的注解是否存在 2.如果存在，首先获取class上面的注解@WXUserInfo 3.在获取方法上面的注解@WXUserInfo 4.注解获取以方法上面为准
         * 5.如果方法上面注解为空，以class上面的注解为准
         */

        // 1 先判断uri是否是js，images还有css。如果是
        // 2 annotation 验证
        // 3 浏览器验证
        // 4 测试验证（在非微信的浏览器，指定openid和额外user信息的能力）
        // 5 scope is base or userinfo check
        // 6 跳转wxurl
        // 如果中间出现异常了，打印异常日志，跳转错误页面

        // LOGGER.debug("preHandle|into RequestURI:" +
        // request.getRequestURI());

        /**
         * 判断当前访问uri是否正常，如果是js，image或者是css 直接返回，如果不是，就验证@WXUserInfo annotation是否存在
         */
        if (FrameworkUtil.isStaticResourceFile(request)) {
            LOGGER.debug("static resource");
            return true;
        }

        WXUserInfo annotation = null;
        Integer isWxUserInfoScope = null;

        if (handler instanceof HandlerMethod) {
            annotation = FrameworkUtil.findAnnotation((HandlerMethod) handler, WXUserInfo.class);
        }

        // 判断annotation是否为空，如果为空，说明方法和类上没有进行@WXUserInfo的注解，就不能被拦截器识别拿到openId
        if (annotation == null) {
            LOGGER.debug("annotion do not match");
            return true;
        }
        // 判断是否是微信浏览器，如果不是微信浏览器，直接跳转到下一下interceptor
        if (!FrameworkUtil.isWXBrower(request,WxConstant.isTestEnable())) {// 不是微信浏览器
            LOGGER.debug("the browser is not wx");
            return true;
        }

        /**
         * 获取当前方法的annotation
         */
        cleanThreadLocal();

        // 判断annotation的scope级别
        if (annotation.obtainWXUserInfoScope() == WXUserInfoScope.USER_INFO) {
            isWxUserInfoScope = 1;
        } else {
            isWxUserInfoScope = 0;
        }

        //
        WXCombinenationInfo wechatCombinenation = new WXCombinenationInfo();
        boolean isOpenidInsession = false;
        // step 1, get wechatCombinenation for test
        boolean isGetforTest=getWechatCombinationForTest(request, isWxUserInfoScope, wechatCombinenation);
        
        // step 2 get wechatCombinenation from session
        // 微信 get openid from session
        WXCombinenationInfo wechatCombinenationfromSession = new WXCombinenationInfo();
        if (getWechatCombinationFromSession(isWxUserInfoScope, wechatCombinenationfromSession,response)) {
            isOpenidInsession = true;
        }
        
        // if 当前请求是 test
        //    if 与之前的request 中的openid不与session中相同，
        //      invalidsession 
        // else
        //    if 之前的请求是test, isTest在session中存在
        //        invalidsession
        if (isGetforTest){
            if (isOpenidInsession){
                //是testopenid,并且两个openid值不相同，说明换用户了，应该对session情况
                if (!StringUtils.equals(wechatCombinenationfromSession.getOpenId(),wechatCombinenation.getOpenId())){
                    isOpenidInsession=false;
                    invalidSessionAndCacheForSpeOpenid(wechatCombinenationfromSession.getOpenId(),response);
                } 
            }
        }else{
            if ("TRUE".equals(request.getSession().getAttribute(WxConstant.TEST_FLAG_ATTRIBUTE_IN_SESSION))&&!FrameworkUtil.isAjax(request)){
                invalidSessionAndCacheForSpeOpenid(wechatCombinenationfromSession.getOpenId(),response);
            }else{
                wechatCombinenation=wechatCombinenationfromSession;
            }
        }


//        
//        if (test){
//            if (session){
//                if (test!=session){
//                    invalide
//                }
//            }
//        }else{
//            
//           wxBean=sessionBean
//        }

        if (!checkSufficent(isWxUserInfoScope, wechatCombinenation)) {
            
            // step 3 判断当前openId是否存在 判断是否是ajax请求
            if (FrameworkUtil.isAjax(request)) {
                return handleAjaxRequest(request, response, isWxUserInfoScope, wechatCombinenation);
            }

            String code = request.getParameter("code");
            String tryCountStr = request.getParameter("trycount");
            int tryCount = FrameworkUtil.stringToInt(tryCountStr);

            if (StringUtils.isNotBlank(code)) {
                //step 4 get open id 等信息从 auth code 
                getWXInfoByWXAuthCode(wechatCombinenation, isWxUserInfoScope, code, tryCount);
            } 
            else {
                // session中没有，又没有code，去微信授权
                /**
                 * 如果根据上面的操作还无法获取openid就进行页面跳转，如果跳转次数超过三次返回错误页面
                 */
                if (!checkSufficent(isWxUserInfoScope, wechatCombinenation)) {
                    return redict2WechatAuthentication(request, response, tryCount, isWxUserInfoScope);
                }
            }
        }

        if (checkSufficent(isWxUserInfoScope, wechatCombinenation)) {
            DataUtils.threadOpenIdDate.set(wechatCombinenation.getOpenId());
            if (!isOpenidInsession) {
                WechatUtil.saveOpenIdToSession(wechatCombinenation.getOpenId());
                HttpSession session = WechatUtil.getSession();
                session.setMaxInactiveInterval(60 * 60 * 24);
            }
            LOGGER.info("finnaly get open id {}", wechatCombinenation.getOpenId());
            return true;
        }

        LOGGER.error("still dont get sufficent wechat information");
        return false;

    }

	private void getWXInfoByWXAuthCode(WXCombinenationInfo wechatCombinenation,Integer isWxUserInfoScope, String code,
			int tryCount) {
		WXUser wxUser = null;
		String openId = null;
		// 如果是微信回调，通过code来交互openid 和 wxuser info
		LOGGER.info("handle wechat callback request, code:{}, obtainWXUserInfoScope():{},tryCount:{}", code,
				isWxUserInfoScope,tryCount);
		switch (isWxUserInfoScope) {
		    case 0:
		        openId = OAuthUtil.getOpenId(code);
		        // userLoginService.synWXOAuthScopeIsBase(code);
		        break;
		    case 1:
		        /**
		         * 1.先scope=base ，拿到openid，根据openId获取user信息， 如果用户信息为空，scope=user_info，拿到用户信息，
		         * 如果用户信息为空，重试3次
		         */
		        if (tryCount == 1) {
		            openId = OAuthUtil.getOpenId(code);
		            wxUser = OAuthUtil.getWxUserInfo(openId);
		        } else {
		            wxUser = OAuthUtil.getWechatUserInfo(code);
		            openId = wxUser.getOpenid();
		        }
	
		        if (wxUser != null) {
		            userLoginService.saveWechatUserInfoToRedis(wxUser);
		        }
	
		        break;
	
		    default:
		        break;
		}
		LOGGER.info("getWXInfoByWXAuthCode, openId:{}, wxUser():{}", openId,
		        wxUser);
		wechatCombinenation.setOpenId(openId);
		wechatCombinenation.setWxUser(wxUser);
	}

	private boolean handleAjaxRequest(HttpServletRequest request,
			HttpServletResponse response, Integer isWxUserInfoScope,
			WXCombinenationInfo wechatCombinenation) throws IOException {
			WXUser wxUser = null;
			String openId = null;
			try {
			    openId = handleAjaxReqeustWithoutOpenidInSession(request, response);
			} catch (WechatAjaxNotSupporttedException e) {
			    LOGGER.error(e.getMessage(), e);
			    response.getWriter()
			            .println("{\"code\":\"" + e.getCode() + "\",\"message\":\"" + e.getMessage() + "\"}");
			    return false;
			}
			if (StringUtils.isNotBlank(openId) && isWxUserInfoScope == 1) {
			    wxUser = WechatUtil.getWechatUserInfoFromRedis(openId);
			}
			wechatCombinenation.setOpenId(openId);
			wechatCombinenation.setWxUser(wxUser);
			if (!checkSufficent(isWxUserInfoScope, wechatCombinenation)) {
			    LOGGER.error("ajax request is not supported to get wechat user info");
			    response.getWriter().println(
			            "{\"code\":\"0001\",\"message\":\"ajax request is not supported to get wechat user info\"}");
			    return false;
			}
			return true;
	
		
	}

	private boolean getWechatCombinationFromSession(Integer isWxUserInfoScope,
			WXCombinenationInfo wechatCombinenation,HttpServletResponse response) {
		String openIdInSession = WechatUtil.getOpenIdFromSession(WechatUtil.getSession());
		if(LOGGER.isDebugEnabled()){
		    LOGGER.debug("isWxUserInfoScope {}, openIdInSession {} ,wechatCombinenation {}",isWxUserInfoScope,openIdInSession,JSON.toJSONString(wechatCombinenation));
		}
        if (StringUtils.isEmpty(openIdInSession)){
            LOGGER.debug("cant get openid from session");
			return false;
		}
        //看从session中获取的openID对应的是否已经被取消关注，如果没有取消关注,flag=true
        boolean flag = checkWechatLoginStatus(openIdInSession,response);
        LOGGER.debug("getWechatCombinationFromSession, checkWechatLoginStatus flag:{}", flag);
        if (flag){
            wechatCombinenation.setOpenId(openIdInSession);
            if (isWxUserInfoScope == 1){
                wechatCombinenation.setWxUser(WechatUtil.getWechatUserInfoFromRedis(openIdInSession));
            }
            LOGGER.debug("current wechat info from session, openid:{}", openIdInSession);
        }
        return flag;
	}

    private boolean getWechatCombinationForTest( HttpServletRequest request,Integer isWxUserInfoScope, WXCombinenationInfo wechatCombinenation) {
        String openidInParameter = request.getParameter("openId");
        LOGGER.debug("isTest {}, openidInParameter {}",WxConstant.isTestEnable,openidInParameter);
        if (WxConstant.isTestEnable && StringUtils.isNotBlank(openidInParameter)) {
            wechatCombinenation.setOpenId(openidInParameter);
            HttpSession session = WechatUtil.getSession();
            session.setAttribute(WxConstant.TEST_FLAG_ATTRIBUTE_IN_SESSION, "TRUE");

            if (isWxUserInfoScope == 1) {
                wechatCombinenation.setWxUser(userLoginService.genenrateWechatTestUserInfo(openidInParameter));
                userLoginService.saveWechatUserInfoToRedis(wechatCombinenation.getWxUser());
            }
            LOGGER.info("wechat test is enable, get openid from url paramter, openid:{}", openidInParameter);
            return true;
        }
        return false;
        
    }

    private String handleAjaxReqeustWithoutOpenidInSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException, WechatAjaxNotSupporttedException {
        // 如果是ajax 请求，并且没有session，从param中取加密后的openId,
        // 如果param中的openId 有效。放置到threadlocal中
        // 如果无效 直接报错
        HttpSession session = WechatUtil.getSession();
        String ajaxOpenId = null;
        if (session.isNew()) {
            String encodeOpenId = request.getParameter(DESUtil.ENCODE_ID);
            LOGGER.info("preHandle|ajax Client,getParameter encodeId ={}", encodeOpenId);
            if (StringUtils.isNotBlank(encodeOpenId)) {
                try {
                    ajaxOpenId = DESUtil.decrypt(encodeOpenId);
                    LOGGER.info("preHandle|ajax Client,decrypt encodeId,ajaxOpenId ={}", ajaxOpenId);

                    // if (StringUtils.isNotBlank(encodeOpenId)) {
                    // //DataUtils.threadOpenIdDate.set(ajaxOpenId);
                    // }else{
                    // response.getWriter().println(
                    // "{\"code\":\"120\",\"message\":\"wechat user no auth!\"}");
                    // return false;
                    // }
                } catch (Exception e) {
                    LOGGER.info("preHandle|ajax Client,decrypt error={}", e.getMessage());
                    response.getWriter().println("{\"code\":\"120\",\"message\":\"wechat user no auth!\"}");

                    throw new WechatAjaxNotSupporttedException("encrypt the open id exception", e);
                }

            } else {
                response.getWriter().println("{\"code\":\"120\",\"message\":\"wechat user no auth!\"}");
                throw new WechatAjaxNotSupporttedException("encodeOpenId is null");
            }
        }

        return ajaxOpenId;
    }

//    private boolean checkSufficent(Integer isWxUserInfoScope, String openid, WXUser wxUser) {
//        if (isWxUserInfoScope == 0) {
//            return StringUtils.isNotBlank(openid);
//        } else {
//            return StringUtils.isNotBlank(openid) && wxUser != null;
//        }
//    }
    
    //检查是否已经满足符合annotation scope的值
    //能否return 
    private boolean checkSufficent(Integer isWxUserInfoScope, WXCombinenationInfo wXCombinenationInfo) {
    	 
        if (isWxUserInfoScope == 0) {
            return StringUtils.isNotBlank(wXCombinenationInfo.getOpenId());
        } else {
            return StringUtils.isNotBlank(wXCombinenationInfo.getOpenId()) && wXCombinenationInfo.getWxUser() != null;
        }
    }

    private boolean redict2WechatAuthentication(HttpServletRequest request, HttpServletResponse response, int tryCount,
            Integer isWxUserInfoScope) throws IOException {
        LOGGER.info("redict2WechatAuthentication,tryCount:{},isWxUserInfoScope:{}", tryCount,isWxUserInfoScope);
        if (!(tryCount > 3)) {
            // TODO 重写 回调的tryCount
            String str = "";
            // if (StringUtils.equals(
            // WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_STATUS,
            // WxConstant.JUMP_STATUS_TRUE)) {
            str = WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_URL_ADDRESS;
            LOGGER.info("go2 wechat authentication page, url:" + str);
            // }

            // 授权三次，如果openid还为空，就抛出异常
            int i = tryCount + 1;
            if (i > 1) {
                LOGGER.warn("wechat authentication retry {}", i);
            }

            // LOGGER.info(
            // "preHandle|obtainWXUserInfoScope:{},wxUser:{},state:{}",
            // annotation.obtainWXUserInfoScope(), wxUser, state);

            // 如果注解是user_info,
            // 并且wxUser为空，
            // 第一次拿不到微信用户信息的情况下，
            // 就让scope等于user_info
            // LOGGER.info(
            // "preHandle|obtainWXUserInfoScope:{},wxUser:{},state:{},isWxUserInfoScope:{}",
            // annotation.obtainWXUserInfoScope(), wxUser, state,
            // isWxUserInfoScope);

            String redrect_url = RequestUtil.generateWxOauthUrlAddress(request, str,
                    (tryCount > 0 && isWxUserInfoScope == 1) ? true : false, String.valueOf(i));
            LOGGER.info("redirect to wechat url:{}", redrect_url);
            response.sendRedirect(redrect_url);
        } else {
            FrameworkUtil.writeErrorOut(response, "/page/error_page_wxopenid.html",null);
        }
        return false;
    }

    private boolean checkWechatLoginStatus(String openid,HttpServletResponse response) {
        String loginStatus = WechatUtil.getCurrentUserLoginStatus(openid);
        if (StringUtils.equals(loginStatus, WxConstant.SAIC_CX_USER_LOGIN_STATUS_NOLOGIN)) {
            LOGGER.info("the wechat login status is cancel,{},{} ", openid, loginStatus);
             invalidSessionAndCacheForSpeOpenid(openid,response);
             return false;
        }
        return true;
    }

    private void  invalidSessionAndCacheForSpeOpenid(String openid,HttpServletResponse response) {
        // remove redis login status
        // WechatUtil.clearCurrentUserSession();
        WechatUtil.removeUserLoginSatus(openid);
        WechatUtil.removeWXUser(openid);
//        WechatUtil
        WechatUtil.logOutAndCleanCookie(response);
//        WechatUtil.saveOpenIdToSession(openid);
       
        LOGGER.info("invalidSessionAndCacheForSpeOpenid clear openid,wxuser,cookie");
    }
    
    

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        cleanThreadLocal();
        /**
         * 判断当前访问uri是否正常，如果是js，image或者是css 直接返回，如果不是，就验证@WXUserInfo annotation是否存在
         */
        if (FrameworkUtil.isStaticResourceFile(request)) {
            return;
        }

        // 判断是否是微信浏览器，如果不是微信浏览器，直接跳转到下一下interceptor
        if (!FrameworkUtil.isWXBrower(request, WxConstant.isTestEnable())) {// 不是微信浏览器
            // TODO log
            return;
        }

        userLoginService.generateWxConfig(request, response);
    }

    private void cleanThreadLocal() {
        DataUtils.threadOpenIdDate.remove();
        DataUtils.threadInvalidSessionDate.remove();
    }
    
    private static class WXCombinenationInfo{
        private WXUser wxUser;
        private String openId;
        
        public WXUser getWxUser() {
            return wxUser;
        }
        public void setWxUser(WXUser wxUser) {
            this.wxUser = wxUser;
        }
        public String getOpenId() {
            return openId;
        }
        public void setOpenId(String openId) {
            this.openId = openId;
        }
        
    }
}