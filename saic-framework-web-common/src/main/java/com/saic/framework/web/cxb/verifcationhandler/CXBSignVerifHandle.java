package com.saic.framework.web.cxb.verifcationhandler;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.saic.ebiz.sso.service.api.ISSOTokenService;
import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.interceptor.verifcationhandler.SignonVerifcationHandler;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.sso.client.SSOClient;
import com.saic.sso.client.SSOInterceptorHelper;

public class CXBSignVerifHandle implements SignonVerifcationHandler {

    private final static Logger logger = LoggerFactory.getLogger(CXBSignVerifHandle.class);

    @Resource(name = "ISSOTokenService")
    private ISSOTokenService iSSOTokenService;

    @Autowired
    private UserLoginService userLoginService;
    
    @Autowired
    private SSOInterceptorHelper ssoInterceptorHelper;

    @Autowired(required=false)
    private SSOClient ssoClient;
    
    public static final String USER_AGENT_CXB = "MongoToC";

    @Override
    public Boolean isMatched(HttpServletRequest request) {
        if(FrameworkUtil.isCXAPPBrower(request))
        	return true;

        return false;
    }   

    private Long getUserid(HttpSession session){
      Long userId=null;
      Object userIdObj=session.getAttribute("userid");
      logger.debug("get userid from session for sso: {}",userIdObj);
      if (userIdObj != null) {
          if(userIdObj instanceof String){
              String userIdStr = (String)userIdObj;
              if (userIdStr != null && !"".equals(userIdStr) && !"-1".equals(userIdStr) && !"0".equals(userIdStr) && NumberUtils.isNumber(userIdStr)) {
                  logger.info("userid is string, userid {}",userId);
                  userId = Long.parseLong(userIdStr);                 
             }
          }
          if(userIdObj instanceof Long){
              logger.info("userid is Long, userid {}",userId);
              userId = (Long)userIdObj;
          }
      }
      return userId;
    }
    @Override
    public Long signProcess(HttpServletRequest request,HttpServletResponse response ,Object handler) {
        //todo 判断 userId 类型 
        // request.getSession().setAttribute("userid", userId); userId 类型为String
//        Long userId = (Long) request.getSession().getAttribute("userid");
        
        
        logger.info("CXBSignVerifHandle signProcess start");
        if (FrameworkUtil.isAjax(request)){
            Long userid=getUserid(request.getSession());
            if (userid!=null){
                return userid;
            }
        }
        Map resultMap =null;
//        Boolean ssoThreadLocalFlag = SSOUtil.haveDisposed.get();
//        if(null == ssoThreadLocalFlag || !ssoThreadLocalFlag){
//            
//        }
        try {
            resultMap = ssoInterceptorHelper.loginInterceptor(request, response);
        } catch (Exception e) {
            logger.error("CXBSignVerifHandle signProcess end, exeption",e);
        } 
        if(resultMap != null){
            Object tempUserId = resultMap.get("userid");
            if(tempUserId != null){
                logger.info("CXBSignVerifHandle signProcess end, tempUserId={}",tempUserId);
                return Long.valueOf(tempUserId.toString());
            }
            logger.info("CXBSignVerifHandle signProcess end, tempUserId is null");
        }
        
        logger.info("CXBSignVerifHandle signProcess end, resultMap is null");
        return null;
        //
    }

    @Override
    public ApplicationChannel getApplicationChannel() {
        return ApplicationChannel.CXB;
    }

    @Override
    public void gotoLogin(HttpServletRequest request, HttpServletResponse response,String callBackUrl) {
        try {
            response.getWriter().append(ssoClient.getAppRedirectTemplate());
            response.flushBuffer();
            return;
        } catch (IOException e) {
            logger.error("CXB go2login error",e);
        }
        
//        if(StringUtils.isEmpty(callBackUrl)){
//            FrameworkUtil.writeErrorOut(response, "/page/cxb_lb_login.html");
//        }else{
//            try {
//                response.sendRedirect(callBackUrl);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        
    }

    @Override
    public String getDefaultCallBackUrl(HttpServletRequest request) {
        return "";
    }

}
