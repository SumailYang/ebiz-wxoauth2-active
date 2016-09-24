/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: OAuthUserInfoServiceImpl.java
 * Author:   zhaohuiliang
 * Date:     2015年10月10日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.saic.ebiz.mdm.api.UserService;
import com.saic.ebiz.mdm.api.WebAccountService;
import com.saic.ebiz.mdm.entity.UserBaseInfoVO;
import com.saic.ebiz.mdm.entity.WebAccountVO;
import com.saic.framework.redis.client.IRedisClient;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.service.OAuthUserInfoService;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Service("oAuthUserInfoService")
public class OAuthUserInfoServiceImpl implements OAuthUserInfoService {

    private Logger logger = LoggerFactory.getLogger(OAuthUserInfoServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private WebAccountService webAccountService;

    @Resource(name = "wxRedisClient")
    public IRedisClient codesClient;

    @Override
    public UserBaseInfoVO selectUserBaseInfoByOpenId(String openId) {
        logger.info("selectUserBaseInfoByOpenId|openId|" + openId);
        long userId = this.getUserIdByOpenId(openId);
        if (0l != userId) {
            UserBaseInfoVO ubiVo = this.findBaseInfoByUserId(userId);
            return ubiVo;
        }
        return null;
    }

    @Override
    public long getUserIdByOpenId(String openId) {
        logger.info("getUserIdByOpenId|openId|" + openId);
        WebAccountVO webAccountVO = this.getWebAccountVOByOpenId(openId);
        if (webAccountVO != null) {
            // webAccountVO
            return webAccountVO.getUserId();
        }
        return 0l;
    }

    @Override
    public UserBaseInfoVO findBaseInfoByUserId(long userId) {
        logger.info("findBaseInfoByUserId|userId|" + userId);
        UserBaseInfoVO user = userService.findBaseInfoByUserId(userId);
        return user;
    }

    @Override
    public WebAccountVO getWebAccountVOByOpenId(String openId) {
        logger.info("getWebAccountVOByOpenId|openId|" + openId);
        WebAccountVO waVo = new WebAccountVO();
        waVo.setNumber(openId);
        List<WebAccountVO> waVos = this.webAccountService.findWebAccountByCondition(waVo);
        logger.info("getWebAccountVOByOpenId|openId|" + openId + "|waVos|" + JSON.toJSONString(waVos));
        if (waVos != null && !waVos.isEmpty()) {
            return waVos.get(0);
        }
        return null;
    }

    private String getWxUserKey(String openId) {
        return WxConstant.SAIC_WX_USER_KEY + openId;
    }

    @Override
    public void setWxUser(WXUser wxUser) {
        String userStr = JSON.toJSONString(wxUser);
        logger.info("setWxUser|user|" + userStr);
        codesClient.setex(getWxUserKey(wxUser.getOpenid()), WxConstant.SAIC_WX_NAMESAPCE,
                WxConstant.SAIC_WX_USER_REDIS_TIME, userStr);
    }

    @Override
    public WXUser getWxUser(String openId) {
        logger.info("getWxUser|openId|" + openId);
        String UserStr = codesClient.get(getWxUserKey(openId), WxConstant.SAIC_WX_NAMESAPCE, null);
        return JSON.parseObject(UserStr, WXUser.class);
    }
    
    @Override
    public Long removeWXUser(String openId) {
        // TODO Auto-generated method stub
        return codesClient.del(getWxUserKey(openId), WxConstant.SAIC_WX_NAMESAPCE);
    }

    @Override
    public void changeUserLoginStatus(String openId, String loginStatus) {
        logger.debug("changeLoginStatus|openId:" + openId + "|loginStatus:" + loginStatus);
        codesClient.setex(WxConstant.SAIC_CX_USER_LOGIN_STATUS_KEY + openId, WxConstant.SAIC_WX_NAMESAPCE,
                WxConstant.SAIC_WX_USER_REDIS_TIME, loginStatus);

    }

    @Override
    public String getUserLoginSatus(String openId) {
        // TODO Auto-generated method stub
        logger.debug("getUserLoginSatus|openId:" + openId);
        return codesClient.get(WxConstant.SAIC_CX_USER_LOGIN_STATUS_KEY + openId, WxConstant.SAIC_WX_NAMESAPCE, null);
    }

    @Override
    public Long removeUserLoginSatus(String openId) {
        // TODO Auto-generated method stub
        return codesClient.del(WxConstant.SAIC_CX_USER_LOGIN_STATUS_KEY + openId, WxConstant.SAIC_WX_NAMESAPCE);
    }
}