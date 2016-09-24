/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: TokenManager.java
 * Author:   zhaohuiliang
 * Date:     2015年9月17日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.support;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.saic.framework.redis.client.IRedisClient;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.api.TicketAPI;
import com.saic.framework.web.wechat.popular.api.TokenAPI;
import com.saic.framework.web.wechat.popular.bean.Ticket;
import com.saic.framework.web.wechat.popular.bean.Token;
import com.saic.framework.web.wechat.service.OAuthUserInfoService;

/**
 * 微信定时任务 - uts 调用lemon-memberService venus服务
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Component("saicWxManager")
public class SaicWxManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(SaicWxManager.class);

    /**
     * 10s
     *
     */
    private static long rotaryTime = 10000;

    private static int rotaryCount = 150;

    @Resource(name = "wxRedisClient")
    public  IRedisClient codesClient;

    @Autowired
    private OAuthUserInfoService oaAuthUserInfoService;

    public void init() {
        init(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET);
    }

    /**
     * 初始化token和ticket
     * 
     * @param appid
     * @param secret
     */
    protected void init(final String appid, final String secret) {
        LOGGER.info("初始化token和ticket ，传入参数[appid:" + appid + "，secret:" + secret + "]");
        // 获取access_token
        String access_token = rotaryToken(appid, secret);
        codesClient.setex(WxConstant.TOKEN_KEY_PREFIX,WxConstant.SAIC_WX_NAMESAPCE,
                WxConstant.SAIC_WX_TOKEN_TIMING_REFRESH_TIME,access_token);
        // 获取ticket
        String ticket = rotaryTicket(appid, access_token);
        codesClient.setex(WxConstant.TICKET_KEY_PREFIX,WxConstant.SAIC_WX_NAMESAPCE, WxConstant.SAIC_WX_TOKEN_TIMING_REFRESH_TIME,ticket);
    }

    /**
     * 轮训请求token
     * 
     * @param appid
     * @param secret
     */
    protected String rotaryTicket(String appid, String access_token) {
        LOGGER.info("rotaryTicket ，初始化tiecket ，传入参数[appid:" + appid + "，access_token:" + access_token + "]");
        if (StringUtils.isBlank(appid) || StringUtils.isBlank(access_token)) {
            return "";
        }
        int i = 0;
        while (true) {
            if (i > rotaryCount) {
                return "";
            }
            try {
                Thread.sleep(rotaryTime);
            } catch (InterruptedException e) {

            }
            Ticket ticket = TicketAPI.ticketGetticket(access_token);
            if (ticket != null) {
                LOGGER.info("rotaryTicket 获取tiecket ，同步参数[appid:" + appid + "，ticket:" + ticket.getTicket() + "]");
                return ticket.getTicket();
            }
            i++;
        }
    }

    /**
     * 轮训请求token
     * 
     * @param appid
     * @param secret
     */
    protected String rotaryToken(String appid, String secret) {
        LOGGER.info("rotaryToken 初始化token ，传入参数[appid:" + appid + "，secret:" + secret + "]");
        if (StringUtils.isBlank(appid) || StringUtils.isBlank(secret)) {
            return "";
        }
        int i = 0;
        while (true) {
            if (i > rotaryCount) {
                return "";
            }
            try {
                Thread.sleep(rotaryTime);
            } catch (InterruptedException e) {

            }
            Token token = TokenAPI.token(appid, secret);
            if (token != null) {
                LOGGER.info(
                        "rotaryToken 获取 token ，同步参数[appid:" + appid + "，access_token:" + token.getAccess_token() + "]");
                return token.getAccess_token();
            }
            i++;
        }
    }

    /**
     * 清除routine session
     * 
     * @return
     */
    public String clearRoutineUserSession(String openId) {
        try {
            LOGGER.info("clearRoutineUserSession|openid:" + openId);
            oaAuthUserInfoService.changeUserLoginStatus(openId, WxConstant.SAIC_CX_USER_LOGIN_STATUS_NOLOGIN);
        } catch (Exception e) {
            LOGGER.error("clearRoutineUserSession fail!", e);
        }
        return "";
    }
}