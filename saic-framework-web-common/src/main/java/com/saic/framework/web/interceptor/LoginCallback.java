package com.saic.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginCallback {
    
    /**
     * 用户未登录时，业务方可以根据需求修改登录页面URL
     * @param channelType 渠道
     * @param request 
     * @param response
     * @param defaultUrl
     * @return
     */
    public String getLoginCallbackUrl(LoginCallbackData callbackData,HttpServletRequest request,HttpServletResponse response);
}
