package com.saic.framework.web.wechat.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saic.framework.web.wechat.popular.bean.WXUser;

public interface UserLoginService {

	/**
	 * 保存wxuser 到 redis中，并保存openId到session中
	 * 
	 * @param openId
	 * @param wxUser
	 */
	public void saveWXUserToRedisAndOpenIdToSession(String openId, WXUser wxUser);

	/**
	 * 准备微信用户测试数据
	 * 
	 * @param request
	 */
	public WXUser genenrateWechatTestUserInfo(String openIdtemp);

	/**
	 * 准备用户测试数据
	 */
//	public void readyTestUserData(HttpServletRequest request);

	/**
	 * 同步微信的用户信息到redis中
	 * 
	 * @param request
	 * @param response
	 */
//	public boolean synWXOAuthScopeIsUserInfo(String code);

	/**
	 * 生成用户登录url
	 * 
	 * @param request
	 * @return
	 */
	public String generateUserLoginUrl(HttpServletRequest request);

	/**
	 * 同步openId到session中
	 * 
	 * @param request
	 * @param response
	 */
//	public String synWXOAuthScopeIsBase(String code);

	/**
	 * 同步userId到session中
	 * 
	 * @param request
	 * @param response
	 */
	public Long getUserIdByOpenId(String openId);

	/**
	 * 生成微信配置参数对象
	 * 
	 * @param request
	 * @param response
	 */
	public void generateWxConfig(HttpServletRequest request, HttpServletResponse response);

	public void saveWechatUserInfoToRedis(WXUser wxUser);
	
}