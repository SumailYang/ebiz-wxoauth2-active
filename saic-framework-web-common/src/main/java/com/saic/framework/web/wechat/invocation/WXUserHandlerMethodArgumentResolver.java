package com.saic.framework.web.wechat.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.util.DataUtils;
import com.saic.framework.web.wechat.util.WechatUtil;

public class WXUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = (Class<?>) parameter.getGenericParameterType();
        if (WXUser.class == clazz) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> clazz = (Class<?>) parameter.getGenericParameterType();
        if (WXUser.class == clazz) {
            WXUser wxUser = WechatUtil.getWechatUserInfoFromRedis(DataUtils.threadOpenIdDate.get());
            if (wxUser != null && (null == wxUser.getSubscribe() || wxUser.getSubscribe() == 0)) {
                WechatUtil.removeWXUser(DataUtils.threadOpenIdDate.get());
            }
            return wxUser;
        }
        return null;
    }
}
