package com.saic.framework.web.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.HtmlUtils;

import com.meidusa.toolkit.common.util.Base64;
import com.saic.ebiz.mdm.metadata.MDMConstants.GLOBAL_BY;
import com.saic.framework.web.constant.Constants;
import com.saic.framework.web.cxb.verifcationhandler.CXBSignVerifHandle;
import com.saic.framework.web.wechat.constant.WxConstant;

/**
 * 
 * @author zhaohuiliang
 *
 */
public class FrameworkUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FrameworkUtil.class);

    /**
     * 判断是否是静态资源文件
     * 
     * @param requestURI
     * @return
     */
    public static boolean isStaticResourceFile(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        boolean flag = false;
        if (StringUtils.isNotBlank(requestURI)) {
            LOGGER.trace("checkIsStaticResourceFile|requestURI" + requestURI);
            if ((requestURI.endsWith(".js") || requestURI.contains("/images/") || requestURI.contains("/css/"))) {
                flag = true;
            }
        }
        LOGGER.trace("check resources is {}", flag);
        return flag;
    }

    /**
     * 分割url参数
     * 
     * @param params
     * @return
     */
    public static Map<String, String> splitURLParams(String params) {
        LOGGER.trace("splitURLParams [params:" + params + "]");
        Map<String, String> paramsMap = new HashMap<String, String>();
        if (StringUtils.isNotBlank(params)) {
            String[] paramsArrays = params.split("&");
            if (paramsArrays != null && paramsArrays.length > 0) {
                for (String param : paramsArrays) {
                    try {
                        String key = param.substring(0, param.indexOf("="));
                        String value = "";
                        if (param.length() > key.length() + 1) {
                            value = param.substring(param.indexOf("=") + 1, param.length());
                        }
                        paramsMap.put(key, value);
                    } catch (Exception e) {
                        LOGGER.warn("split param error [params:" + param + "]", e);
                    }
                }
            }
        }
        return paramsMap;
    }

    /**
     * 判断是否是ajax请求
     * 
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        boolean falg = request.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
        //LOGGER.info("isAjax|falg:" + falg);
        return falg;
    }

    /**
     * 判断是否是微信
     * 
     * @param request
     * @return
     */
    public static boolean isWXBrower(HttpServletRequest request, boolean isTestEnable) {
        boolean flag = request.getHeader("User-Agent") != null
                && request.getHeader("User-Agent").toLowerCase().indexOf("micromessenger") > -1;
        
        if (!flag){
        	if (isTestEnable){
        		if (request.getParameter("test") !=null){
        			flag=true;
        		}else{
        		    HttpSession session=WechatUtil.getSession();
        		    if ("TRUE".equals(session.getAttribute(WxConstant.TEST_FLAG_ATTRIBUTE_IN_SESSION))){
        		        flag=true; 
        		    }
        		}
        		
        	}
        }
        LOGGER.debug("User-Agent:{}",request.getHeader("User-Agent"));
        return flag;
    }

    /**
     * 判断是否是微信
     * 
     * @param request
     * @return
     */
    public static boolean isWXTestBrower(HttpServletRequest request) {
        boolean flag = request.getHeader("User-Agent") != null
                && request.getHeader("User-Agent").toLowerCase().indexOf("test") > -1;
        //LOGGER.info("i:" + flag);
        return flag;
    }

    
    /**
     * 字符串转数字
     * 
     * @param value
     * @return
     */
    public static Long stringToLong(String value) {
        LOGGER.trace("stringToLong|value:" + value);
        Long num = 0L;
        try {
            if (StringUtils.isNotBlank(value)) {
                num = Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("stringToLong|value:" + value, e);
            num = 0L;
        }
        LOGGER.trace("stringToLong|num:" + num);
        return num;
    }

    /**
     * 字符串转数字
     * 
     * @param value
     * @return
     */
    public static int stringToInt(String value) {
        LOGGER.trace("stringToInt|value:" + value);
        int num = 0;
        try {
            if (StringUtils.isNotBlank(value)) {
                num = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.trace("stringToInt|value:" + value, e);
            num = 0;
        }
        LOGGER.trace("stringToInt|value:" + num);
        return num;
    }

    /**
     * 判断是否是数字
     * 
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        try {
            LOGGER.trace("isInteger|value:" + value);
            if (StringUtils.isNotBlank(value)) {
                Integer.parseInt(value);
            }
            return true;
        } catch (NumberFormatException e) {
            LOGGER.error("isInteger|value:" + value, e);
            return false;
        }
    }

    /**
     * 判断是否是数字
     * 
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        LOGGER.trace("isNum|str:" + str);
        return StringUtils.isNotBlank(str) ? str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") : false;
    }

    /**
     * 获取类和方法上的注解，如果类和方法上都有注解，默认返回方法上的注解，如果方法上没有，则使用类上面的注解
     * 
     * @param method
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A findAnnotation(HandlerMethod method, Class<A> annotationType) {
        A methodAnnotation = AnnotationUtils.findAnnotation(method.getMethod(), annotationType);
        if (methodAnnotation == null) {
            A beanTypeAnnotation = AnnotationUtils.findAnnotation(method.getBeanType(), annotationType);
            LOGGER.trace("findAnnotation|beanTypeAnnotation："
                    + (beanTypeAnnotation == null ? " is null" : beanTypeAnnotation.annotationType()));
            return beanTypeAnnotation;
        }
        LOGGER.trace("findAnnotation|methodannotation："
                + (methodAnnotation == null ? " is null" : methodAnnotation.annotationType()));
        return methodAnnotation;
    }

    /**
     * 输出错误页面
     * 
     * @param response
     * @param path 页面路径
     */
    public static void writeErrorOut(HttpServletResponse response, String path,String content) {
        LOGGER.debug("writeErrorOut|page：" + path);
        InputStream is = FrameworkUtil.class.getResourceAsStream(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer strb = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                if(str.indexOf("{{spChannel}}") > -1 && StringUtils.isNotBlank(content)){
                    str = str.replace("{{spChannel}}", content);
                }
                strb.append(str + "\r\n");
            }
//            if(StringUtils.isEmpty(content)){
//                strb.append(content);
//            }
            br.close();

            response.setCharacterEncoding("utf-8");
            response.getWriter().println(strb.toString());

        } catch (UnsupportedEncodingException e) {
            LOGGER.error("writeErrorOut|page：" + path, e);
        } catch (IOException e) {
            LOGGER.error("writeErrorOut|page：" + path, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error("writeErrorOut|BufferedReader close error", e);
                }
                br = null;
            }
        }

    }
    
    /**
     * 判断是否是车享APP
     * 
     * @param request
     * @return
     */
    public static boolean isCXAPPBrower(HttpServletRequest request) {
    	String token = request.getParameter(Constants.WEBCOMMON_TOKEN_PARAM_NAME);
        String sign = request.getParameter(Constants.WEBCOMMON_SIGN_PARAM_NAME);
        String userAgent = request.getHeader("User-Agent");
        String source = request.getParameter("source");
        String v = request.getParameter(Constants.WEBCOMMON_VERSION_PARAM_NAME);
        if(v == null){
            v = "0.1";
        }
        LOGGER.trace("CXBSignVerifHandle.isMatched|v:{},token:{},sign:{},source:{}", v, token, sign, source);
        if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(sign)){
        	return true;
        }else if(StringUtils.contains(userAgent, CXBSignVerifHandle.USER_AGENT_CXB)){
        	return true;
        }else if(StringUtils.isNotEmpty(userAgent) && StringUtils.equals(source, GLOBAL_BY.CHEXIANG_BAO.getCode())){
        	return true;
        }
        
    	return false;
    }
    
    /**
     * 从cookie中获取用户Token
     * 
     * @param request
     * @return
     */
    public static String getWapCookieToken(HttpServletRequest request){
    	Cookie[] cookieArray = (Cookie[])request.getCookies();;
    	if(cookieArray != null){
    		for(int i = 0;i < cookieArray.length;i++){
    			if(cookieArray[i].getName().equalsIgnoreCase("sc_s")){
    				return cookieArray[i].getValue();
    			}
    		}
    	}
		return null;
    }
    
    /**
     * 生成重定向地址
     * 
     * @param request
     * @return
     */
    public static String constructRedirectUrl(String loginUrl,HttpServletRequest request){
    	String backUrl = request.getRequestURL().toString();

        if (request.getQueryString() != null) {
            backUrl += "?" + request.getQueryString();
        }
        
        String addr = Base64.encode(request.getRemoteAddr().getBytes());
        addr = HtmlUtils.htmlEscape(addr);

        try {
			backUrl = URLEncoder.encode(backUrl, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage());
		}
        LOGGER.trace("URLEncodeRequestURI: " + backUrl);
        backUrl = HtmlUtils.htmlEscape(backUrl);
        LOGGER.trace("***HtmlUtilsEscape: " + backUrl);
    	
    	return loginUrl+ "?addr=" + addr + "&backUrl=" + backUrl;
    }
    
    /**
     * 判断是否是WAP渠道
     * 
     * @param request
     * @return
     */
    public static boolean isWAPBrower(HttpServletRequest request) {
    	if(!isWXBrower(request,false) && !isCXAPPBrower(request)){
    		return true;
    	}
    	
    	return false;
    }

}