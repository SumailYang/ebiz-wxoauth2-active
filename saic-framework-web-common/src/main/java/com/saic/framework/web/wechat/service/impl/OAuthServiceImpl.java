/*
	 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: OAuthServiceImpl.java
 * Author:   zhaohuiliang
 * Date:     2015年10月9日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.saic.framework.redis.client.IRedisClient;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.exception.WeiXinRequestBackException;
import com.saic.framework.web.wechat.popular.api.SnsAPI;
import com.saic.framework.web.wechat.popular.api.UserAPI;
import com.saic.framework.web.wechat.popular.bean.SnsToken;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.popular.util.JsUtil;
import com.saic.framework.web.wechat.service.OAuthService;
import com.saic.framework.web.wechat.util.TicketUtil;
import com.saic.framework.web.wechat.util.TokenUtil;
import com.saic.framework.web.wechat.vo.WxConfigVo;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Service("oAuthService")
public class OAuthServiceImpl implements OAuthService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OAuthServiceImpl.class);

    @Resource(name = "wxRedisClient")
    public IRedisClient codesClient;

    @Override
    public String getOpenId(String code) {
        SnsToken token = SnsAPI.oauth2AccessToken(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET, code);
        if (token == null) {
            return null;
        }
        LOGGER.debug("OAuthUtil.getOpenId传入参数[code:" + code + ",appId:" + WxConstant.SAIC_WX_APPID + ",serret:"
                + WxConstant.SAIC_WX_APPSECRET + "]");

        return token.getOpenid();
    }

    @Override
    public String getSignature(String url) {
        String jsapi_ticket = TicketUtil.getTicket();
        String signature = JsUtil.generateConfigSignature(WxConstant.JS_NONCE_STR, jsapi_ticket,
                WxConstant.JS_TIMESTAMP, url);
        return signature;
    }

    @Override
    public String getToken(String tokenId) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString() + "--rz";
        String redisflag = codesClient.setex(token, WxConstant.SAIC_WX_NAMESAPCE, WxConstant.REDISTIME, tokenId);
        LOGGER.debug("getToken|redisflag=" + redisflag + ",setOpenIdToken返回token=" + token + ",OpenId:" + tokenId);
        return token;
    }

    @Override
    public WxConfigVo getWxConfigJson(String url) {
        String jsapi_ticket = TicketUtil.getTicket();
        long timestamp = System.currentTimeMillis() / 1000;
        // long timestamp = 1414587457L;
        String nonceStr = UUID.randomUUID().toString();
        // String nonceStr = "Wm3WZYTPz0wzccnW";

        String signature = JsUtil.generateConfigSignature(nonceStr, jsapi_ticket, timestamp + "", url);
        LOGGER.debug("url:{},timestamp:{},nonceStr:{},signature:{},jsapi_ticket:{}", url, timestamp, nonceStr,
                signature, jsapi_ticket);
        // TODO Auto-generated method stub
        return new WxConfigVo(false, WxConstant.SAIC_WX_APPID, timestamp + "", nonceStr, signature, null);
    }

    @Override
    public String getWxConfigJson(String url, boolean debug, List<String> jsApiList) {
        String jsapi_ticket = TicketUtil.getTicket();
        if (jsApiList == null) {
            jsApiList = new ArrayList<String>();
        }
        return JsUtil.generateConfigJson(jsapi_ticket, debug, WxConstant.SAIC_WX_APPID, url,
                jsApiList.toArray(new String[jsApiList.size()]));
    }

    @Override
    public WXUser getWxUserInfo(String openId) {
        LOGGER.debug("getWxUserInfo|no width access_token-openId:{}", openId);
        WXUser user = null;
        try {
            String access_token = TokenUtil.getAccess_Token();

            /**
             * 重试三次 第一次，拿到user信息，如果用不为空，用户错误吗不为42001和40001，就跳出循环
             * 
             * 
             */
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    LOGGER.debug("getWxUserInfo|access_token:{}:current retry count:{}", access_token, i);
                } else {
                    LOGGER.warn("getWxUserInfo|access_token:{}:current retry count:{}", access_token, i);
                }

                user = UserAPI.userInfo(access_token, openId);

                if (!(user == null || StringUtils.equals("42001", user.getErrcode())
                        || StringUtils.equals("40001", user.getErrcode()))) {
                    if (StringUtils.isNotBlank(user.getErrcode())) {
                        throw new WeiXinRequestBackException(user.getErrcode(), user.getErrmsg());
                    }
                    break;
                }

                if (StringUtils.equals("42001", user.getErrcode()) || StringUtils.equals("40001", user.getErrcode())) {
                    access_token = TokenUtil.getAccess_token1(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getWxUserInfo|exception:", e);
        }

        if (user == null) {
            throw new WeiXinRequestBackException("0000001", "can't obtain wechat user info,open id " + openId);
        }

        LOGGER.debug("getWxUserInfo|user:{}", user);
        return user;
    }

    @Override
    public WXUser getWxUserInfo(String openId, String access_token) {
        LOGGER.debug("getWxUserInfo|with access_token-openId:{},access_token:{}", openId, access_token);
        WXUser user = null;
        try {
            for (int i = 0; i < 3; i++) {
                user = SnsAPI.userinfo(access_token, openId, "zh_CN");
                if (!(user == null || StringUtils.equals("42001", user.getErrcode())
                        || StringUtils.equals("40001", user.getErrcode()))) {

                    if (StringUtils.isNotBlank(user.getErrcode())) {
                        throw new WeiXinRequestBackException(user.getErrcode(), user.getErrmsg());
                    }

                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("getWxUserInfo|exception:", e);
        }
        LOGGER.debug("getWxUserInfo|user:{}", user);
        return user;
    }
}