package com.saic.framework.web.invocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.saic.framework.web.annotation.UserId;
import com.saic.framework.web.wechat.util.DataUtils;

public class UserIdHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserIdHandlerMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.hasParameterAnnotation(UserId.class)) {
            try {
                String source = (String) webRequest.getAttribute("source", RequestAttributes.SCOPE_REQUEST);
                LOGGER.debug("resolveArgument[source:{}]", source);
//                if (StringUtils.equals(source, GLOBAL_BY.CHEXIANG_BAO.getCode())) {
//                    String userIdStr = (String) webRequest.getAttribute(Constants.USER_CXB_USERID,
//                            RequestAttributes.SCOPE_REQUEST);
//                    LOGGER.info("resolveArgument[userId:{}]", userIdStr);
//                    
//                    return FrameworkUtil.stringToLong(userIdStr);
//                } else if (StringUtils.equals(source, GLOBAL_BY.WEIXIN_SRV_NUM.getCode())) {
//                    return UserUtil.getCurrentUserId();
//                } else {
//                    return UserUtil.getCurrentUserId();
//                }
                return   DataUtils.threadUserIdData.get();
            } catch (Exception e) {
                LOGGER.error("resolveArgument|exception!", e);
                return null;
            }
        }
        return null;
    }
}