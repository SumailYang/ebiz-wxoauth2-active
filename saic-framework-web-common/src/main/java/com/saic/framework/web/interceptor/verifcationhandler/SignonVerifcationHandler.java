package com.saic.framework.web.interceptor.verifcationhandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saic.framework.web.annotation.SignonVerification;
import com.saic.framework.web.constant.ApplicationChannel;


public interface SignonVerifcationHandler {
	/**
	 * 业务处理类渠道标识，各实现类渠道可以相同
	 * @return
	 */
	public ApplicationChannel getApplicationChannel();
	/**
	 * 业务实现类是否处理请求,拦截器只执行第一个匹配的业务处理逻辑，多个业务同时匹配非第一个匹配不会执行
	 * @param request
	 * @return
	 */
	public Boolean isMatched(HttpServletRequest request);
	/**
	 * 登录业务逻辑处理,当isMatched=true时，拦截器会调用 signProcess,处理具体的业务逻辑
	 * @param request
	 * @param annotation
	 * @return 成功登录返回userId,错误返回null
	 */
	public Long signProcess(HttpServletRequest request,HttpServletResponse response, Object handler);
	/**
	 * 返回登录或者错误页面，当业务逻辑处理验证不成功，且注解redict2Login=true时，拦截器会调用此方法
	 * @param request
	 * @param response
	 */
	public void gotoLogin(HttpServletRequest request,HttpServletResponse response,String callBackUrl);
	
	
	public String getDefaultCallBackUrl(HttpServletRequest request);
	
}
