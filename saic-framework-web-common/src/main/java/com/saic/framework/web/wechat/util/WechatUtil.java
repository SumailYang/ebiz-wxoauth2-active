/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: UserUtil.java
 * Author:   zhaohuiliang
 * Date:     2015年10月12日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.util;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.saic.ebiz.mdm.entity.UserBaseInfoVO;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.service.OAuthUserInfoService;
import com.saic.sso.client.SSOClient;
import com.saic.sso.client.SSOUtil;
import com.saic.sso.client.constant.Constants;
import com.saic.sso.client.entity.LoginInfo;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Component
public class WechatUtil {
    
    private static SSOClient ssoClient;

    private static Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    private static OAuthUserInfoService oaAuthUserInfoService;
    
    static String ssologoutFlag = "SSO_USER_LOGOUT"; 

    @Resource(name = "oAuthUserInfoService")
    public void setOaAuthUserInfoService(OAuthUserInfoService oaAuthUserInfoService) {
        WechatUtil.oaAuthUserInfoService = oaAuthUserInfoService;
    }
    
    
    public SSOClient getSsoClient() {
        return ssoClient;
    }


    @Autowired(required = true)
    public void setSsoClient(SSOClient ssoClient) {
        WechatUtil.ssoClient = ssoClient;
    }


    /**
     * 获取当前openId
     * 
     * @return
     */
    public static String getCurrentUserOpenId() {
        String openId = null;
        openId = DataUtils.threadOpenIdDate.get();
        if (StringUtils.isNotBlank(openId)) {
            HttpSession session = getSession();
            return getOpenIdFromSession(session);
        } else {
            logger.debug("thread local have openid, {}", openId);
        }
        logger.debug("getCurrentUserOpenId|openId:" + openId);
        return openId;
    };

    
    public static String getOpenIdFromSession(HttpSession  session) {
    	logger.debug("thread local don't have openid, try to get open id from session, session id {}",
                session.getId());
        String openId = (String) session.getAttribute(WxConstant.SESSION_SAIC_WX_OPENID);
        return openId;
    }
    
    
    /**
     * 获取当前的ApplicationChannel
     * 
     * @return
     */
    public static String getCurrentApplicationChannel() {
        String ApplicationChannel = (String) getSession().getAttribute(WxConstant.SESSION_SAIC_APPLICATIONCHANNEL);
        logger.debug("getCurrentApplicationChannel|ApplicationChannel:" + ApplicationChannel);
        return ApplicationChannel;
    };

    /**
     * 获取微信用户
     * 
     * @return
     */
    public static WXUser getWechatUserInfoFromRedis(String openId) {
    	if (StringUtils.isNotBlank(openId)){
	        WXUser wxUser = oaAuthUserInfoService.getWxUser(openId);
	        logger.debug("getCurrentWxUser|WXUser:" + wxUser);
	        return wxUser;
        }
    	return null;
    };

    private static boolean isSSOSessionPrority(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String sp=request.getParameter(com.saic.framework.web.constant.Constants.IS_SSO_SESSION_PRORITY);
        if (StringUtils.equals(sp, "1")){
            logger.debug("sso session proority is high");
            return true;
        }
        return false;
    }
    /**
     * 获取当前用户id
     * 
     * @return
     */
    public static Long getCurrentUserId() {
        Long userId = null;
        userId = DataUtils.threadUserIdData.get();
        if (userId == null || userId <= 0L) {
//            if (isSSOSessionPrority()){
//                userId=getSSOUserid();
//                if (userId != null && userId > 0L){
//                    logger.debug("get userid from session for sso: {}",userId);
//                    return userId;
//                }
//            }
            
//            HttpSession session = getSession();
//            logger.debug("thread local don't have userid, try to get user id from session, session id {}",
//                    session.getId());
//            userId = (Long) session.getAttribute(WxConstant.SESSION_SAIC_CX_USERID);
//            HttpServletRequest request = getHttpRequst();
//            if(!FrameworkUtil.isAjax(request)){//非ajax请求 判断session过期时间
////               if(sessionIsInvaild(session)){
////                   logger.info("is not ajax request, session is invaild");
////                   userId = null;
////               }
//                
//                
//            }
//            logger.debug("get userid from session for wx: {}",userId);
            String logoutFlag = DataUtils.threadInvalidSessionDate.get();
            logger.debug("getCurrentUserId userid:{},threadInvalidSessionDate-logoutFlag:{}",userId,logoutFlag);
            if (userId==null && !"1".equals(logoutFlag)){
                logger.debug("getCurrentUserId getSSOUserid,flag={}","1".equals(logoutFlag));
                userId=getSSOUserid();
            }
        }
        logger.debug("getCurrentUserId|userId:" + userId);
        return userId;
    };
    
    
    public static boolean sessionIsInvaild(HttpSession session){
        final Long createTimeInvaild = 60*1000*2L;//todo UCM 配置
        Long sessionCreateTime =(Long) session.getAttribute(WxConstant.SESSION_CREATE_TIME_KEY);//获取usersession创建时间
        if(null == sessionCreateTime){
            return true;
        }
        logger.info("sessionIsInvaild sessionCreateTime={}",sessionCreateTime);
        Long currentTime = System.currentTimeMillis();
        if(currentTime - sessionCreateTime > createTimeInvaild){
            return true;
        }else{
            return false;
        }
        
    }
    
    public static Long getSSOUserid(){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
        .getRequest();
        Long userId=null;
        
//        Boolean haveDisposed=SSOUtil.haveDisposed.get();
//        logger.debug("haveDisposed: {}" , haveDisposed);
//        if(haveDisposed==null || !haveDisposed){
//           String cookieUserId = getUserIdfromCooke(request);
//            if (userId != null && !"".equals(userId) && !"-1".equals(userId)) {
//                userId = Long.parseLong(cookieUserId);
//             }
            String tokenValue = SSOUtil.getCookieToken(request);
            logger.debug("getSSOUserid[SSOUtil.getCookieToken],cookievalue: " + tokenValue);
            if (StringUtils.isNotBlank(tokenValue)) {
                String userIDCookie = ssoClient.validateToken(tokenValue, "");
                if (userIDCookie != null && !"".equals(userIDCookie) && !"-1".equals(userIDCookie) && NumberUtils.isNumber(userIDCookie)) {
                    logger.info("ssoClient.validateToken, userid {}",userIDCookie);
                    userId = Long.parseLong(userIDCookie);                 
               }
            }
//          }
          
        return userId;

        
//        
//        Long userId=null;
//        
//  
//        Object userIdObj=getSession().getAttribute(Constants.SESSION_USERID);
//        logger.debug("get userid from session for sso: {}",userIdObj);
//        if (userIdObj != null) {
//            if(userIdObj instanceof String){
//                String userIdStr = (String)userIdObj;
//                if (userIdStr != null && !"".equals(userIdStr) && !"-1".equals(userIdStr) && NumberUtils.isNumber(userIdStr)) {
//                    logger.info("userid is string, userid {}",userId);
//                    userId = Long.parseLong(userIdStr);                 
//               }
//            }
//            if(userIdObj instanceof Long){
//                logger.info("userid is Long, userid {}",userId);
//                userId = (Long)userIdObj;
//            }
//        }
//        return userId;
    }
    /**
     * 获取UserBaseInfoVO
     * 
     * @return
     */
    public static UserBaseInfoVO getCurrentUserInfo(Long userId) {
        if (userId != null && userId > 0l) {
            UserBaseInfoVO userBaseInfoVo = oaAuthUserInfoService.findBaseInfoByUserId(userId);
            logger.debug("getCurrentUserInfo|userId:{},userBaseInfoVo:{}", userId, JSON.toJSONString(userBaseInfoVo));
            return userBaseInfoVo;
        }
        return null;
    };

    /**
     * 获取当前UserBaseInfoVO
     * 
     * @return
     */
    public static UserBaseInfoVO getCurrentUserInfo() {
        if (getCurrentUserId() != null) {
            UserBaseInfoVO userBaseInfoVO = getCurrentUserInfo(getCurrentUserId());
            return userBaseInfoVO;
        }
        return null;
    };

    /**
     * 清空当前用户的session信息
     */
    public static void clearCurrentUserSession() {
        logger.debug("clearCurrentUserSession|userIdkey:" + WxConstant.SESSION_SAIC_CX_USERID );
        getSession().removeAttribute(WxConstant.SESSION_SAIC_CX_USERID);
//        getSession().removeAttribute(WxConstant.SESSION_SAIC_WX_OPENID);
    }

    /**
     * 拿到当前用户的登录状态
     * 
     * @return
     */
    public static String getCurrentUserLoginStatus(String openId) {
        String loginStatus = oaAuthUserInfoService.getUserLoginSatus(openId);
        logger.debug("getCurrentUserLoginStatus|openId:{},loginStatus:" + loginStatus, openId);
        return loginStatus;
    }

    public static void saveOpenIdToSession(String openId) {

        HttpSession session = getSession();
        session.setAttribute(WxConstant.SESSION_SAIC_WX_OPENID, openId);
        logger.debug("save Open Id to Session, session id:{}, openid:{}", session.getId(), openId);

        // saveUserLoginTokenToSession(openId);
    }

    // public static void saveUserLoginTokenToSession(String userLoginToken) {
    // logger.debug("saveUserLoginTokenToSession|save UserLoginToken:" + userLoginToken);
    // getSession().setAttribute(WxConstant.SESSION_SAIC_CX_ULOGIN_TOKEN, userLoginToken);
    // }

    public static void saveUserIdToSession(Long userId,HttpServletRequest request,HttpServletResponse response) {

        HttpSession session = getSession();
        session.setAttribute(WxConstant.SESSION_SAIC_CX_USERID, userId);
        //已和陈亮组的开发确认loginByApp参数为1
        LoginInfo loginInfo = ssoClient.loginByApp(1, userId.toString(), true, request.getRemoteHost(), request, response);
//        Long currentTime = System.currentTimeMillis();
//        session.setAttribute(WxConstant.SESSION_CREATE_TIME_KEY, currentTime);
//        session.setAttribute(Constants.SESSION_USERID, userId);
        
        logger.debug("save userId to Session, session id:{}, userid:{},loginInfo[rtnCode]", session.getId(), userId,loginInfo.getRtnCode());
        // logger.debug("saveUserIdToSession|save userId:" + userId);

        oaAuthUserInfoService.changeUserLoginStatus(getCurrentUserOpenId(), WxConstant.SAIC_CX_USER_LOGIN_STATUS_LOGIN);
    }
    
    
    public static long removeWXUser(String openId){
        return oaAuthUserInfoService.removeWXUser(openId);
    }

    public static Long removeUserLoginSatus(String openId) {
        return oaAuthUserInfoService.removeUserLoginSatus(openId);
    }

    public static void changeUserLoginStatus(String openId, String loginStatus) {
		oaAuthUserInfoService.changeUserLoginStatus(openId, loginStatus);
	}

	/**
     * 获取当前session
     * 
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession(false);
        if (session==null){
        	session=request.getSession();
            logger.debug("create session, sessionid:{}", session.getId());
        }else{
            logger.debug("getSession|sessionId:" + session.getId());
        }
        return session;
    }

    
    public static HttpServletRequest getHttpRequst() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }
    
    
    public static void logOutAndCleanCookie(HttpServletResponse response){
        HttpServletRequest request = getHttpRequst();
        DataUtils.threadInvalidSessionDate.set("1");
        ssoClient.logout(request, response);
        logger.debug("logOutAndCleanCookie|setAttribute[ssologoutFlag]:{}","1");
        logger.debug("logOutAndCleanCookie|getAttribute[get-ssologoutFlag1]:{}",request.getSession().getAttribute(ssologoutFlag));
        logger.debug("logOutAndCleanCookie|getAttribute[get-ssologoutFlag2]:{}",getHttpRequst().getSession().getAttribute(ssologoutFlag));
    }

    
    public static void main(String[] args) {
    }
}