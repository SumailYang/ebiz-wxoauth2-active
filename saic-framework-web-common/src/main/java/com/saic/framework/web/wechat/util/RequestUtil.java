package com.saic.framework.web.wechat.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saic.ebiz.mdm.metadata.MDMConstants.GLOBAL_BY;
import com.saic.framework.web.constant.Constants;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.api.SnsAPI;

/**
 * 
 * 请求工具类<br>
 * 
 * <pre>
 * 1.当前请求的url
 * 2.生成用户登录地址
 * 3.生成微信授权地址
 * 4.解析车享宝的参数
 * 
 * </pre>
 *
 * @author zhaohuiliang
 * @see HttpServletRequest
 * @since 20160106
 */
public class RequestUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);

    /**
     * 生成网页授权url地址 1.如果需要跳转，URLEncoder.encode当前的连接，在拼接跳转地址 2.如果不需要跳转，直接拼接当前地址
     * 
     * @param currenturl 当前url地址
     * @return
     */
    public static String generateWxOauthUrlAddress(HttpServletRequest request, String mainSkipUrl,
            boolean snsapi_userinfo, String state) {
        String oauthUrl = null;
        try {
            LOGGER.debug("generateWxOauthUrlAddress|mainSkipUrl:{},snsapi_userinfo:{},state:{}", mainSkipUrl,
                    snsapi_userinfo, state);
            String currenturl = null;
            String skipUrl = getCurrentRequestUrlAddress(request);
            if(StringUtils.contains(skipUrl, "?")){
            	skipUrl = skipUrl+"&trycount="+state;
            }else{
            	skipUrl = skipUrl+"?trycount="+state;
            }
            if (StringUtils.isNotBlank(mainSkipUrl)) {
                currenturl = mainSkipUrl.replace("{backurl}", URLEncoder.encode(skipUrl, "utf-8"));
            } else {
                currenturl = skipUrl;
            }
            oauthUrl = SnsAPI.connectOauth2Authorize(WxConstant.SAIC_WX_APPID, currenturl, snsapi_userinfo, state);
            LOGGER.debug("generateWxOauthUrlAddress|wx_redirect_url:" + oauthUrl);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("generateWxOauthUrlAddress|UnsupportedEncodingException|", e);
        }
        return oauthUrl;
    }

    /**
     * 生成用户登录url地址
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String generateUserLoginUrlAddress(HttpServletRequest request, String token) {
        String redirect_url = WxConstant.SAIC_USERLOGIN_URL.replace("{openid}", token).replace("{backUrl}",
                URLEncoder.encode(getCurrentRequestUrlAddress(request))) + "&timestamp=" + (new Date().getTime());
        LOGGER.info("generateUserLoginUrlAddress|login_redirect_url:" + redirect_url + "|token:" + token);
        return redirect_url;
    }

    /**
     * 得到当前请求url地址
     * 
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    public static String getCurrentRequestUrlAddress(HttpServletRequest request) {
        // String currentURI = request.getRequestURI();
        String currentURI = request.getRequestURL().toString();
        LOGGER.info("getCurrentRequestUrlAddress|getCurrentRequestAllUrlAddress:{}" , getCurrentRequestAllUrlAddress(request));
        Enumeration paramNames = request.getParameterNames();
        int i = 0;
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            if (StringUtils.equals(paramName.toLowerCase(), "code")
                    || StringUtils.equals(paramName.toLowerCase(), "state")) {
                continue;
            }
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    if (i == 0) {
                        currentURI += "?" + paramName + "=" + paramValue;
                    } else {
                        currentURI += "&" + paramName + "=" + paramValue;
                    }
                    i++;
                }
            }
        }

        // currentURI = WxConstant.SAIC_BASE_URL + currentURI;
        LOGGER.debug("getCurrentRequestUrlAddress|currenturl:" + currentURI);
        return currentURI;
    }

    /**
     * 得到当前请求全url地址
     * 
     * @param request
     * @return
     */
    public static String getCurrentRequestAllUrlAddress(HttpServletRequest request) {
        String currentURL = request.getRequestURL().toString();
        currentURL = currentURL
                + (StringUtils.isNotBlank(request.getQueryString()) ? ("?" + request.getQueryString()) : "");
        LOGGER.info("get current visit all url address|currenturl:{}" , currentURL);
        return currentURL;
    }

    /**
     * 
     * 功能描述: <br>
     * 
     * <pre>
     * 解析车享宝传来的参数,
     * 从ftl页面传参，
     * 对url 传参的时候是cxbParams
     * 对form 传参的时候是cxbParamsEncode
     * 
     * 1.拿到cxbParams的参数，
     *      如果参数为空，在拿cxbParamsEncode参数
     *      如果参数不为空，对参数进行decode。
     *      解析cxbparam，之后拿到uid和source
     *      
     * 2.如果cxbParams和cxbParamsEncode都为空，直接拿uid和source
     * 
     * 
     * </pre>
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static void analysisRequestParametersByCxb(HttpServletRequest request) throws UnsupportedEncodingException {

        String cxbparam = request.getParameter(Constants.USER_CXB_PARAMS);
        String cxbparamEncode = request.getParameter(Constants.USER_CXB_PARAMS_ENCODE);

        String uid = request.getParameter("uid");
        String source = request.getParameter("source");

        LOGGER.debug("analysisRequestParametersByCxb|cxbparams-:{},cxbparams-encode:{},uid:{},source:{}", cxbparam,
                cxbparamEncode, uid, source);

        /**
         * <pre>
         * 如果cxbparam，cxbparamEncode，uid都为空，
         * 存在两种原因：1，没有登录，2.是微信请求
         * 1.没有登录的话，请求登录，设置source，不需要解析设置
         * 2.微信请求不走#analysisRequestParametersByCxb方法
         * 
         * 默认设置source
         * </pre>
         */
        if (StringUtils.isBlank(cxbparam) && StringUtils.isBlank(cxbparamEncode) && StringUtils.isBlank(uid)) {
            // 设置source的值，默认source = 7（为微信），如果source=2为车享宝
            request.setAttribute("source",
                    StringUtils.isNotBlank(source) ? source : GLOBAL_BY.WEIXIN_SRV_NUM.getCode());
            return;
        }

        /**
         * 如果cxbparam或者是cxbparamEncode不为空的话，进行车享宝参数的解析
         */
        if (StringUtils.isNotBlank(cxbparam) || StringUtils.isNotBlank(cxbparamEncode)) {
            if (StringUtils.isBlank(cxbparam) && StringUtils.isNotBlank(cxbparamEncode)) {
                cxbparam = URLDecoder.decode(cxbparamEncode, "utf-8");
                LOGGER.debug("analysisRequestParametersByCxb|cxbparams-decode-After:{}", cxbparam);
            }

            cxbparam = cxbparam.indexOf("%") > -1 ? URLDecoder.decode(cxbparam, "utf-8") : cxbparam;
            Map<String, String> params = FrameworkUtil.splitURLParams(cxbparam);
            uid = params.get("uid");
            source = params.get("source");
        }

        LOGGER.debug("analysisRequestParametersByCxb|uid:{},source:{}", uid, source);

        if (StringUtils.isNotBlank(uid)) {
            String userId = AESUtils.decryptData(uid);
            LOGGER.info("analysisRequestParametersByCxb|uid:{},userId:{}", uid, userId);
            request.setAttribute(Constants.USER_CXB_USERID, userId);
        }

        // 设置source的值，默认source = 7（为微信），如果source=2为车享宝
        request.setAttribute("source", StringUtils.isNotBlank(source) ? source : GLOBAL_BY.WEIXIN_SRV_NUM.getCode());

        /**
         * 判断当前的source是车享宝,是拿到cxbparam ，如果不存在，就拿当前的getQueryString 设置cxbParameter到setAttribute中
         */
        if (StringUtils.equals(source, GLOBAL_BY.CHEXIANG_BAO.getCode())) {
            String cxbParameter = cxbparam;
            if (StringUtils.isBlank(cxbparam)) {
                cxbParameter = request.getQueryString();
            } else {
                cxbParameter = cxbparam.indexOf("%") > -1 ? URLDecoder.decode(cxbparam, "utf-8") : cxbparam;
            }

            String appVersion = request.getHeader(Constants.CXB_APP_APPVERSION);
            String cxbParameterEncode = URLEncoder.encode(cxbParameter, "utf-8");
            LOGGER.info("analysisRequestParametersByCxb|cxbParameter:{},cxbParameterEncode:{},appVersion:{}",
                    cxbParameter, cxbParameterEncode, appVersion);
            request.setAttribute(Constants.USER_CXB_PARAMS, cxbParameterEncode);
            request.setAttribute(Constants.USER_CXB_APPVERSION, appVersion);
        }
    }
}