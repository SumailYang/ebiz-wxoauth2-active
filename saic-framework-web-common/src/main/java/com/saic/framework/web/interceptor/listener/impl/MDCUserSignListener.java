package com.saic.framework.web.interceptor.listener.impl;

import com.meidusa.venus.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.meidusa.venus.util.VenusTracerUtil;
import com.saic.framework.web.interceptor.listener.UserSignData;
import com.saic.framework.web.interceptor.listener.UserSignListener;

public class MDCUserSignListener implements UserSignListener {
    private final Logger tracklog = LoggerFactory.getLogger("UIdLogInterceptor");

    private final String EQ_DELIMITER = "=";
    private final String USER_ID_KEY = "userId";
    private final String VENUS_TRACE_ID_KEY = "vtId";
    private final String SPLIT_DELIMITER_LEFT = "[";
    private final String SPLIT_DELIMITER_RIGHT = "]";
    private final String NOT_DEFINED = "NA";

    @Override
    public void execute(UserSignData param) {
        tracklog.debug("MDCUserSignListener execute start,the param={}", param);
        if (null == param) {
            return;
        }
        Long userSignOnId = param.getUserId();
        String userSignOnChannel = param.getChannel();
        String uIdStr = NOT_DEFINED;

        if (null != userSignOnId && Long.parseLong(userSignOnId.toString()) > 0L) {
            uIdStr = userSignOnId.toString();
        }
        
        String uuid = null;
        byte[] vtIdArr = VenusTracerUtil.getTracerID();
        if(vtIdArr == null){
        	uuid = java.util.UUID.randomUUID().toString();
        }else{
        	uuid = new UUID(vtIdArr).toString();
        }

        String vti_token = VENUS_TRACE_ID_KEY + EQ_DELIMITER + uuid;
        MDC.put(VENUS_TRACE_ID_KEY, vti_token);
        String uid_token = USER_ID_KEY + EQ_DELIMITER + uIdStr + SPLIT_DELIMITER_LEFT + userSignOnChannel
                + SPLIT_DELIMITER_RIGHT;
        MDC.put(USER_ID_KEY, uid_token);
        tracklog.debug("MDCUserSignListener execute end");
        return;
    }

    @Override
    public void complateCallback() {
        tracklog.debug("MDCUserSignListener execute end");
        MDC.remove(USER_ID_KEY);
        MDC.remove(VENUS_TRACE_ID_KEY);
    }

}
