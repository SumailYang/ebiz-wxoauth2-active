package com.saic.framework.web.wechat.invocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.saic.framework.web.wechat.annotation.OpenId;
import com.saic.framework.web.wechat.util.WechatUtil;

public class OpenIdHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenIdHandlerMethodArgumentResolver.class);

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(OpenId.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
	    LOGGER.debug("OpenIdHandlerMethodArgumentResolver[resolveArgument] start");
		if (parameter.hasParameterAnnotation(OpenId.class)) {
		    LOGGER.debug("OpenIdHandlerMethodArgumentResolver[resolveArgument] the openId ={}",WechatUtil.getCurrentUserOpenId());
			return WechatUtil.getCurrentUserOpenId();
		}
		return null;
	}

}
