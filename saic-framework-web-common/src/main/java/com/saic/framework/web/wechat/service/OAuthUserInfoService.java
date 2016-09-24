/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: OAuthUserInfoService.java
 * Author:   zhaohuiliang
 * Date:     2015年10月10日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.service;

import com.saic.ebiz.mdm.entity.UserBaseInfoVO;
import com.saic.ebiz.mdm.entity.WebAccountVO;

import com.saic.framework.web.wechat.popular.bean.WXUser;

/**
 * <br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
public interface OAuthUserInfoService {

    /*********
     * 获取用户信息
     * 
     * @param openId
     * @return
     */
    public UserBaseInfoVO selectUserBaseInfoByOpenId(String openId);

    /**
     * 根据openId获取userId
     * 
     * @param openId wxoauth 用户唯一标识openId
     * @return
     */
    public long getUserIdByOpenId(String openId);

    /*******
     * 获取车享UserBaseInfoVO
     * 
     * @param userId 用户id
     * @return info is null 用户不存在
     */
    public UserBaseInfoVO findBaseInfoByUserId(long userId);

    /*********
     * 获取 绑定对象
     * 
     * @param openId
     * @return
     */
    public WebAccountVO getWebAccountVOByOpenId(String openId);

    /**
     * 设置微信的用户信息
     * 
     * @param wxUser
     */
    public void setWxUser(WXUser wxUser);

    /**
     * 根据openId 获取微信用户信息
     * 
     * @param openId 微信用户唯一标识
     * @return
     */
    public WXUser getWxUser(String openId);

    /**
     * 
     * 功能描述: <br>
     * 删除wxuser对象
     *
     * @param openId
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public Long removeWXUser(String openId);

    /**
     * 改变用户登录状态
     * 
     * @param userId 用户id
     * @param loginStatus 登录状态
     */
    public void changeUserLoginStatus(String openId, String loginStatus);

    /**
     * 拿到当前的用户状态
     * 
     * @param userId
     * @return
     */
    public String getUserLoginSatus(String openId);

    /**
     * 删除当前的用户状态
     * 
     * @param userId
     * @return
     */
    public Long removeUserLoginSatus(String openId);
}
