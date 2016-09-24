package com.saic.framework.web.invocation;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.saic.ebiz.mdm.entity.UserBaseInfoVO;
import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.constant.Constants;
import com.saic.framework.web.wechat.util.DataUtils;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.framework.web.wechat.util.WechatUtil;

public class UserBaseInfoHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserBaseInfoHandlerMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = (Class<?>) parameter.getGenericParameterType();
        if (clazz == UserBaseInfoVO.class) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> clazz = (Class<?>) parameter.getGenericParameterType();
        if (clazz == UserBaseInfoVO.class) {
            try {
                String source = DataUtils.threadSourceData.get();
                LOGGER.debug("resolveArgument[source:{}]", source);
                if (StringUtils.equals(source, ApplicationChannel.WECHAT.toString())) {
                    return WechatUtil.getCurrentUserInfo();
                } else if (StringUtils.equals(source,ApplicationChannel.CXB.toString())) {
//                    String userIdStr = (String) webRequest.getAttribute(Constants.USER_CXB_USERID,
//                            RequestAttributes.SCOPE_REQUEST);
                	 Long userId =  DataUtils.threadUserIdData.get();
                    LOGGER.info("resolveArgument[userId:{}]", userId);
                    return WechatUtil.getCurrentUserInfo(userId);
                } else {
                    return WechatUtil.getCurrentUserInfo();
                }
            } catch (Exception e) {
                LOGGER.error("resolveArgument|exception!", e);
                return null;
            }
        }
        return null;
    }

}
