/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: TokenUtil.java
 * Author:   zhaohuiliang
 * Date:     2015年9月18日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.util;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//import com.saic.ebiz.mms.common.util.member.MemberRedisClient;
import com.saic.framework.redis.client.IRedisClient;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.api.TokenAPI;
import com.saic.framework.web.wechat.popular.bean.Token;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Component("wxMMSWebTokenUtil")
public class TokenUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);

    // private static MemberRedisClient springRedisClient;
    public static IRedisClient codesClient;

    @Resource(name = "wxRedisClient")
    public void setIRedisClient(IRedisClient redisClient) {
        TokenUtil.codesClient = redisClient;
    }

    public static String getKey(String appid) {
        return WxConstant.TOKEN_KEY_PREFIX;
    }

    public static String getAccess_token1(String appid, String secret) {
        Token token = TokenAPI.token(appid, secret);
        if (token == null || StringUtils.isBlank(token.getAccess_token())) {
            return null;
        }
        
        LOGGER.info("TokenUtil|getAccess_token1|key:" + getKey(appid) + "|value:" + token.getAccess_token());
        codesClient.setex(getKey(appid), WxConstant.SAIC_WX_NAMESAPCE, WxConstant.SAIC_WX_TOKEN_TIMING_REFRESH_TIME,
                token.getAccess_token());

        codesClient.del(WxConstant.TICKET_KEY_PREFIX, WxConstant.SAIC_WX_NAMESAPCE);
        TicketUtil.getTicket2(appid, token.getAccess_token());
        
        return codesClient.get(getKey(appid), WxConstant.SAIC_WX_NAMESAPCE, null);
    }

    /**
     * 根据appId拿到token
     * 
     * @param appid
     * @return
     */
    public static String getAccess_Token(String appid, String appsecret) {
        String access_token = codesClient.get(getKey(appid), WxConstant.SAIC_WX_NAMESAPCE, null);
        LOGGER.info("TokenUtil|getAccess_Token|redis|key:" + getKey(appid) + "|value:" + access_token);

        /*
         * boolean flag = false; if (StringUtils.isNotBlank(access_token)) { CallBackIp callBackIp =
         * TokenAPI.getCallBackIp(access_token); if (callBackIp == null || StringUtils.equals("42001",
         * callBackIp.getErrcode()) || StringUtils.equals("40001", callBackIp.getErrcode())) { flag = true; } else {
         * LOGGER.info("TokenUtil|getCallBackIp|redis|ips:" + callBackIp.getIp_list()); } } else { flag = true; }
         * LOGGER.info("TokenUtil|getCallBackIp|redis|flag:" + flag);
         */

        if (StringUtils.isBlank(access_token)) {
            access_token = getAccess_token1(appid, appsecret);
        }
        return access_token;
    }

    /**
     * 拿到access_toekn
     * 
     * @return
     */
    public static String getAccess_Token() {
        return getAccess_Token(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET);
    }
}