package com.saic.framework.web.wechat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.saic.framework.web.wechat.annotation.WXUserInfo;
import com.saic.framework.web.wechat.annotation.WXUserInfoScope;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.service.UserLoginService;
import com.saic.framework.web.wechat.util.FrameworkUtil;
import com.saic.framework.web.wechat.util.RequestUtil;
import com.saic.framework.web.wechat.util.WechatUtil;

/**
 * 
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author wangjiayan
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CopyOfWXUserInfoInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(CopyOfWXUserInfoInterceptor.class);

    @Autowired
    private UserLoginService userLoginService;

    private boolean isTest = false;

    public void setIsTest(boolean isTest) {
        this.isTest = isTest;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        LOGGER.debug("preHandle|currenturl:" + RequestUtil.getCurrentRequestUrlAddress(request));

        // 添加注解判断
        /**
         * 1.判断当前访问url对应的注解是否存在 2.如果存在，首先获取class上面的注解@WXUserInfo 3.在获取方法上面的注解@WXUserInfo 4.注解获取以方法上面为准
         * 5.如果方法上面注解为空，以class上面的注解为准
         */

        // 1 先判断uri是否是js，images还有css。如果是
        // 2 annotation 验证
        // 3 浏览器验证
        // 4 测试验证（在非微信的浏览器，指定openid和额外user信息的能力）
        // 5 scope is base or userinfo check
        // 6 跳转wxurl
        // 如果中间出现异常了，打印异常日志，跳转错误页面

        // LOGGER.debug("preHandle|into RequestURI:" +
        // request.getRequestURI());

        /**
         * 判断当前访问uri是否正常，如果是js，image或者是css 直接返回，如果不是，就验证@WXUserInfo annotation是否存在
         */
        if (FrameworkUtil.isStaticResourceFile(request)) {
            return true;
        }

        /**
         * 获取当前方法的annotation
         */
        WXUserInfo annotation = null;
        if (handler instanceof HandlerMethod) {
            annotation = FrameworkUtil.findAnnotation((HandlerMethod) handler, WXUserInfo.class);
            
        }
        

        // 判断annotation是否为空，如果为空，说明方法和类上没有进行@WXUserInfo的注解，就不能被拦截器识别拿到openId
        if (annotation == null) {
            return true;
        }

        // 判断是否是微信浏览器，如果不是微信浏览器，直接跳转到下一下interceptor
//        if (!FrameworkUtil.isWXBrower(request)) {// 不是微信浏览器
//            // TODO log
//            if (annotation.weChatOnly()) {
//                FrameworkUtil.writeErrorOut(response, "/page/error_page_wxopenid.html");
//                return false;
//            } else {
//                return true;
//            }
//        }
        
        //微信
        String openid = WechatUtil.getCurrentUserOpenId();
//        String loginStatus = WechatUtil.getCurrentUserLoginStatus();
        String loginStatus =null;
        LOGGER.info("preHandle|openid：{},loginStatus:{}", openid, loginStatus);

        /**
         * 判断当前openId是否存在
         */
        if (StringUtils.isNotBlank(openid)
                && StringUtils.equals(loginStatus, WxConstant.SAIC_CX_USER_LOGIN_STATUS_LOGIN)) {
            return true;
        } else {
            WechatUtil.clearCurrentUserSession();
        }

        /**
         * 判断是否是ajax请求
         */
        if (FrameworkUtil.isAjax(request)) {
            if (annotation.weChatOnly()) {
                response.getWriter().println("{\"code\":\"120\",\"message\":\"wechat user no auth!\"}");
                // TODO log
                return false;
            } else {
                return true;
            }
        }

        /**
         * 判断如果是测试的话，准备wx的user数据
         */
        if (isTest) {
            String openId = request.getParameter("openId");
            if (StringUtils.isNotBlank(openId)) {
//                userLoginService.readyTestWXUserData(openId);
                return true;
            }
        }


        LOGGER.info("preHandle|annotation：" + annotation.annotationType());

        String code = request.getParameter("code");

        try {
            /**
             * annotation.obtainWXUserInfoScope() 判断怎么获取wxuserinfo
             */
            LOGGER.info("preHandle|code：{}|annotation.obtainWXUserInfoScope():{}", code,
                    annotation.obtainWXUserInfoScope());
            if (StringUtils.isNotBlank(code)) {
                switch (annotation.obtainWXUserInfoScope()) {
                    case BASE:
//                        userLoginService.synWXOAuthScopeIsBase(code);
                        break;
                    case USER_INFO:
                        /**
                         * 1.先scope=base ，拿到openid，根据openId获取user信息， 如果用户信息为空，scope=user_info，拿到用户信息， 如果用户信息为空，重试3次
                         */
//                        userLoginService.synWXOAuthScopeIsUserInfo(code);
                        break;
                    default:
                        break;
                }
            }

            openid = WechatUtil.getCurrentUserOpenId();

            /**
             * 如果根据上面的操作还无法获取openid就进行页面跳转，如果跳转次数超过三次返回错误页面
             */
            if (StringUtils.isBlank(openid)) {
                String stateStr = request.getParameter("state");
                LOGGER.info("preHandle|openid：" + openid + "|state:" + stateStr);
                int state = FrameworkUtil.stringToInt(stateStr);
                if (!(state > 3)) {
                    String str = "";
                    if (StringUtils.equals(WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_STATUS, WxConstant.JUMP_STATUS_TRUE)) {
                        str = WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_URL_ADDRESS;
                        LOGGER.info("preHandle|jumpurl：" + str);
                    }
                    // 授权三次，如果openid还为空，就抛出异常
                    int i = state + 1;

                    // 设置scope属性，false为base，true为user_info
                    boolean isScope = false;

                    // 拿到微信user
                    WXUser wxUser =null;// WechatUtil.getCurrentWxUser();

                    LOGGER.info("preHandle|obtainWXUserInfoScope：{},wxUser:{},state:{}",
                            annotation.obtainWXUserInfoScope(), wxUser, state);

                    // 如果注解是user_info,
                    // 并且wxUser为空，
                    // 第一次拿不到微信用户信息的情况下，
                    // 就让scope等于user_info
                    if (annotation.obtainWXUserInfoScope() == WXUserInfoScope.USER_INFO
                            && (wxUser == null || StringUtils.isBlank(wxUser.getOpenid())) && state > 0) {
                        isScope = true;
                    }

                    LOGGER.info("preHandle|obtainWXUserInfoScope：{},wxUser:{},state:{},isScope:{}",
                            annotation.obtainWXUserInfoScope(), wxUser, state, isScope);

                    String redrect_url = RequestUtil.generateWxOauthUrlAddress(request, str, isScope,
                            String.valueOf(i));
                    response.sendRedirect(redrect_url);
                } else {
                    FrameworkUtil.writeErrorOut(response, "/page/error_page_wxopenid.html",null);
                }
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(
                    "preHandle|synchronization wechat user infomation error![code:" + code + ",openId:" + openid + "]",
                    e);
            FrameworkUtil.writeErrorOut(response, "/page/error_page_synwx.html",null);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        /**
         * 判断当前访问uri是否正常，如果是js，image或者是css 直接返回，如果不是，就验证@WXUserInfo annotation是否存在
         */
        if (FrameworkUtil.isStaticResourceFile(request)) {
            return;
        }

        // 判断是否是微信浏览器，如果不是微信浏览器，直接跳转到下一下interceptor
//        if (!FrameworkUtil.isWXBrower(request)) {// 不是微信浏览器
//            // TODO log
//            return;
//        }

        userLoginService.generateWxConfig(request, response);
    }

}