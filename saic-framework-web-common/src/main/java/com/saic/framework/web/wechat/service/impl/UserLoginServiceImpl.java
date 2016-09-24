package com.saic.framework.web.wechat.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.service.OAuthUserInfoService;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.DataUtils;
import com.saic.framework.web.wechat.util.IDGenerate;
import com.saic.framework.web.wechat.util.OAuthUtil;
import com.saic.framework.web.wechat.util.RequestUtil;
import com.saic.framework.web.wechat.util.WechatUtil;
import com.saic.framework.web.wechat.vo.WxConfigVo;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    @Autowired
    private OAuthUserInfoService oauthUserInfoService;

//    @Override
//    public boolean synWXOAuthScopeIsUserInfo(String code) {
//        try {
//            String openId = null;
//            String access_token = null;
//            SnsToken token = SnsAPI.oauth2AccessToken(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET, code);
//            openId = token.getOpenid();
//            access_token = token.getAccess_token();
//            WXUser wxUser = OAuthUtil.getWxPageOAuthUserInfo(openId, access_token);
//            
//            LOGGER.info("get wechat user info,code:{},openId:{},access_token:{}",code,openId,access_token);
//            saveWXUserToRedisAndOpenIdToSession(openId, wxUser);
//        } catch (Exception e) {
//            LOGGER.error("synWXOAuthScopeIsUserInfo|exception[code:" + code + "]", e);
//        }
//        return true;
//    }

    public void saveWXUserToRedisAndOpenIdToSession(String openId, WXUser wxUser) {
        LOGGER.debug("saveWXUserToRedisAndOpenIdToSession|openId:{},wxUser:{}", openId, wxUser);
        WechatUtil.saveOpenIdToSession(openId);
        DataUtils.threadOpenIdDate.set(openId);
        if (wxUser != null && StringUtils.isNotBlank(wxUser.getOpenid())) {
            oauthUserInfoService.setWxUser(wxUser);
        }
    }
    
    public void saveWechatUserInfoToRedis(WXUser wxUser) {
        oauthUserInfoService.setWxUser(wxUser);
    }

 
//    @Override
//    public String synWXOAuthScopeIsBase(String code) {
//    	String openId = null;
//        try {
//            
//            if (StringUtils.isNotBlank(code)) {
//                openId = OAuthUtil.getOpenId(code);
//            }
//            // TODO return false 的处理
//            if (StringUtils.isBlank(openId)) {
//                LOGGER.error("can't get openid from code, code:{}",code);
//                return null;
//            }
//            saveWXUserToRedisAndOpenIdToSession(openId, null);
//        } catch (Exception e) {
//            LOGGER.error("synWXOAuthScopeIsBase|exception[code:" + code + "]", e);
//        }
//        return openId;
//    }

    public Long getUserIdByOpenId(String openId) {
        Long userId = null;
        if (StringUtils.isNotBlank(openId)) {
            userId = oauthUserInfoService.getUserIdByOpenId(openId);
        }

        LOGGER.debug("synUserIdToSession|userId:{},openId:{}", userId, openId);
        return userId;
    }

    @Override
    public void generateWxConfig(HttpServletRequest request, HttpServletResponse response) {
        WxConfigVo wxConfigVo = OAuthUtil.getWxConfigJson(RequestUtil.getCurrentRequestAllUrlAddress(request));
        LOGGER.debug("generateWxConfig|wxConfigVo:{}", wxConfigVo);
        request.setAttribute("wxConfigVo", wxConfigVo);
    }

    @Override
    public WXUser genenrateWechatTestUserInfo(String openIdtemp) {
        WXUser user = new WXUser();
        user.setCity("上海");
        user.setCountry("中国");
        user.setSex(1);
        user.setGroupid(0);
        user.setHeadimgurl("");
        user.setLanguage("zh_CN");
        user.setNickname("test三");
        user.setOpenid(openIdtemp);
        user.setProvince("上海");
        user.setSubscribe(1);
        user.setSubscribe_time(1450254845);
        LOGGER.debug("readyTestWXUserData|isTest|openId:{},wxuser:{}", openIdtemp, user);
        return user;
        //saveWXUserToRedisAndOpenIdToSession(openIdtemp, user);
    }

//    @Override
//    public void readyTestUserData(HttpServletRequest request) {
//        String userIdStr = request.getParameter("userId");
//        LOGGER.debug("readyTestUserData|userIdStr:{}", userIdStr);
//        Long userId = (long) FrameworkUtil.stringToLong(userIdStr);
//        WechatUtil.saveUserIdToSession(userId);
//    }

    @Override
    public String generateUserLoginUrl(HttpServletRequest request) {
        String userLogintoken = WechatUtil.getCurrentUserOpenId();
        String token = IDGenerate.getUUID();
        if (StringUtils.isNotBlank(userLogintoken)) {
            token = userLogintoken;
        }
        token = OAuthUtil.getLoginToken(token);
        LOGGER.info("generateUserLoginUrl|token:{}", token);
        String reString = RequestUtil.generateUserLoginUrlAddress(request, token);
        return reString;
    }
}