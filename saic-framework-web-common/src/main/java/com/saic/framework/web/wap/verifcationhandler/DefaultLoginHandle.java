package com.saic.framework.web.wap.verifcationhandler;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import com.saic.ebiz.sso.service.api.ISSOTokenService;
import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.interceptor.verifcationhandler.SignonVerifcationHandler;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.sso.client.SSOInterceptorHelper;

public class DefaultLoginHandle implements SignonVerifcationHandler {

    private final static Logger logger = LoggerFactory.getLogger(DefaultLoginHandle.class);

    @Resource(name = "ISSOTokenService")
    private ISSOTokenService iSSOTokenService;

    @Autowired
    private UserLoginService userLoginService;
    
    @Value("${ebiz.m.site.loginUrl}")
    private String loginUrl;
    
    @Autowired
    private SSOInterceptorHelper ssoInterceptorHelper;
    
    @Override
    public Boolean isMatched(HttpServletRequest request) {
    	if(FrameworkUtil.isWAPBrower(request))
        	return true;

        return false;
    }   

    @Override
    public Long signProcess(HttpServletRequest request,HttpServletResponse response, Object handler) {
        logger.info("DefaultLoginHandle signProcess start");
        Map resultMap = null;
        try {
            resultMap = ssoInterceptorHelper.loginInterceptor(request, response);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("DefaultLoginHandle signProcess end, Exception",e);
        } 
        if(resultMap != null){
            Object tempUserId = resultMap.get("userid");
            if(tempUserId != null){
                logger.info("DefaultLoginHandle signProcess end, tempUserId={}",tempUserId);
                return Long.valueOf(tempUserId.toString());
            }
            logger.info("DefaultLoginHandle signProcess end, tempUserId is null");
        }
        
        logger.info("DefaultLoginHandle signProcess end, resultMap is null");
       return null;
    }

    @Override
    public ApplicationChannel getApplicationChannel() {
        return ApplicationChannel.WAP;
    }

    @Override
    public void gotoLogin(HttpServletRequest request, HttpServletResponse response,String callBackUrl) {
    	try {
    	    if(StringUtils.isEmpty(callBackUrl)){
    	        response.sendRedirect(FrameworkUtil.constructRedirectUrl(loginUrl, request));
    	    }else{
    	        response.sendRedirect(callBackUrl);
    	    }
		} catch (IOException e) {
			logger.error(e.getMessage());
		};
    }

    @Override
    public String getDefaultCallBackUrl(HttpServletRequest request) {
        return loginUrl;
    }

}
