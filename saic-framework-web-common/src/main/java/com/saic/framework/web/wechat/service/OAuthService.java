/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: OAuthService.java
 * Author:   zhaohuiliang
 * Date:     2015年10月9日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.service;

import java.util.List;

import com.saic.framework.web.wechat.vo.WxConfigVo;

import com.saic.framework.web.wechat.popular.bean.WXUser;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
public interface OAuthService {

	/**
	 * 网页授权，根据code获取openid
	 * 
	 * @param code
	 * @return
	 */
	public String getOpenId(String code);

	/**
	 * 根绝url获取签名
	 * 
	 * @param url
	 * @return
	 */
	public String getSignature(String url);

	/**
	 * 根绝tokenid存储到redis中，保证sso登录不失效
	 * 
	 * @param tokenId
	 * @return
	 */
	public String getToken(String tokenId);

	/**
	 * 
	 * 生成wxconfig的json配置
	 * 
	 * @param url
	 *            当前访问url
	 * @param debug
	 *            调试模式
	 * @param jsApiList
	 *            可访问的方法列表
	 * @return
	 */
	public String getWxConfigJson(String url, boolean debug, List<String> jsApiList);

	/**
	 * 根据url返回当前的微信配置vo对象
	 * 
	 * @param url
	 * @return
	 */
	public WxConfigVo getWxConfigJson(String url);

	/**
	 * 根据openId 获取wx的用户信息
	 * 
	 * @param openId
	 *            用户唯一标识
	 * @return
	 */
	public WXUser getWxUserInfo(String openId);

	/**
	 * 网页授权获取用户信息
	 * 
	 * @param openId
	 * @param access_token
	 * @return
	 */
	public WXUser getWxUserInfo(String openId, String access_token);
}
