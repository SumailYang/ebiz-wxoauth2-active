/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: TicketUtil.java
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
import com.saic.framework.redis.client.IRedisClient;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.api.TicketAPI;
import com.saic.framework.web.wechat.popular.bean.Ticket;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Component("wxMMSWebTicketUtil")
public class TicketUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(TicketUtil.class);

//    private static MemberRedisClient springRedisClient;
//
//    @Resource(name = "wxMmsWebMemberRedisClient")
//    public void setSpringRedisClient(MemberRedisClient springRedisClient) {
//        TicketUtil.springRedisClient = springRedisClient;
//    }
    
    public static IRedisClient codesClient;

    @Resource(name = "wxRedisClient")
    public void setIRedisClient(IRedisClient redisClient) {
    	TicketUtil.codesClient = redisClient;
    }

    public static String getKey(String appid) {
        return WxConstant.TICKET_KEY_PREFIX;
    }

    /**
     * 获取 jsapi ticket
     * 
     * @param appid
     * @return
     */
    protected static String getTicket(String appid) {
        String jsapitikect = codesClient.get(getKey(appid),WxConstant.SAIC_WX_NAMESAPCE,null);
        LOGGER.info("TicketUtil|getTicket|redis|key:" + getKey(appid) + "|value:" + jsapitikect);
        if (StringUtils.isBlank(jsapitikect)) {
            return geTicket1(appid);
        }
        return jsapitikect;
    }

    public static String getTicket2(String appid, String access_token) {
        Ticket ticket = TicketAPI.ticketGetticket(access_token);
        if (ticket == null || StringUtils.isBlank(ticket.getTicket())) {
            return null;
        }

        LOGGER.info("TicketUtil|geTicket1|key:" + getKey(appid) + "|value:" + ticket.getTicket());
        codesClient.setex(getKey(appid), WxConstant.SAIC_WX_NAMESAPCE, WxConstant.SAIC_WX_TOKEN_TIMING_REFRESH_TIME,ticket.getTicket());
        return codesClient.get(getKey(appid),WxConstant.SAIC_WX_NAMESAPCE,null);
    }

    protected static String geTicket1(String appid) {
        String access_token = TokenUtil.getAccess_Token(appid, WxConstant.SAIC_WX_APPSECRET);
        if (StringUtils.isBlank(access_token)) {
            return null;
        }

        return getTicket2(appid, access_token);
    }

    /**
     * 获取 jsapi ticket
     * 
     * @param appid
     * @return
     */
    public static String getTicket() {
        return getTicket(WxConstant.SAIC_WX_APPID);
    }
}
