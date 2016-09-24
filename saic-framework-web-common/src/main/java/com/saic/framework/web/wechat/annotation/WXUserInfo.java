package com.saic.framework.web.wechat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 保证能够得到对应的微信用户信息，有两个scope
 * 
 * 微信公共安全注解，用于生成签名和认证
 * 
 * @author zhaohuiliang
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WXUserInfo {

	/**
	 * <pre>
	 * obtainWXUserInfoScope = WXUserInfoScope.BASE 
	 * 	微信浏览器跳转基础授权认证，Scope为snsapi_base，获取openId。
	 * 
	 * obtainWXUserInfoScope = WXUserInfoScope.USERINFO 
	 * 	在未关注的微信公共号的情况下，微信浏览器跳转高级授权认证，Scope为snsapi_userinfo，获取openId和微信用户信息。
	 * 
	 * </pre>
	 * 
	 * @return
	 * @see WXUserInfoScope
	 */
	WXUserInfoScope obtainWXUserInfoScope() default WXUserInfoScope.BASE;
	
	/**
	 * 如果没有
	 * 
	 */
	boolean weChatOnly() default false;
}