package com.saic.framework.web.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.saic.framework.web.annotation.ApplicationChannelParameter;
import com.saic.framework.web.wechat.util.DataUtils;

public class ApplicationChannelHandlerMethodArgumentResolver implements
		HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ApplicationChannelParameter.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		if (parameter.hasParameterAnnotation(ApplicationChannelParameter.class)) {
			return DataUtils.threadSourceData.get();
		}
		return null;
	}

}
