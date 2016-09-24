package com.saic.framework.web.cxb.verifcationhandler;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saic.ebiz.mdm.metadata.MDMConstants.GLOBAL_BY;
import com.saic.framework.web.constant.ApplicationChannel;
import com.saic.framework.web.constant.Constants;
import com.saic.framework.web.interceptor.verifcationhandler.SignonVerifcationHandler;
import com.saic.framework.web.wechat.util.AESUtils;
import com.saic.framework.web.wechat.util.FrameworkUtil;


public class CopyOfCXBUrlSignVerifHandle implements SignonVerifcationHandler {
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(CopyOfCXBUrlSignVerifHandle.class);

	public Boolean isMatched(HttpServletRequest request) {
		 String cxbparam = request.getParameter(Constants.USER_CXB_PARAMS);
	     String cxbparamEncode = request.getParameter(Constants.USER_CXB_PARAMS_ENCODE);
	     
	     String uid = request.getParameter("uid");
	     String source = request.getParameter("source");
	     LOGGER.info("CXBUrlSignVerifHandle.isMatched|uid:{},source:{},cxbparam:{},cxbparamEncode:{}",uid,source,cxbparam,cxbparamEncode);
	     
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
	     if (StringUtils.isEmpty(source)) {
	    	 if(StringUtils.isNotBlank(cxbparam) || StringUtils.isNotBlank(cxbparamEncode)){
	    		 return true;
	    	 }
		}else if (source.equals(GLOBAL_BY.CHEXIANG_BAO.getCode())) {
	            return true;
	        }
	     
		return false;
	}

	@Override
	public Long signProcess(HttpServletRequest request, HttpServletResponse response ,Object handler) {
		
		 // 解析车享宝参数，第一次传过来source
        // http://member.pre.com/memberRights/index.htm?
        // source=2&uid=yldTyJMWtA/tSy2eYCuAxQ==&userId=11005741&longitude=121.428692&latitude=31.206892&userToken=MDEyMzU2OGM4NTA5NTZiNDEyMDkxMTAwNTc0Mc_e9swRP754e6evH-n6LQ2FEjEJ
        // 拿到uid和source存放到attribute中
		
        try {
			//cxbparam url提交
			//cxbparamEncode form 提交
			 String cxbparam = request.getParameter(Constants.USER_CXB_PARAMS);
		     String cxbparamEncode = request.getParameter(Constants.USER_CXB_PARAMS_ENCODE);
		     
		     String uid = request.getParameter("uid");
		     String source = request.getParameter("source");

		      LOGGER.info("CXBUrlSignVerifHandle.signProcess|cxbparams-:{},cxbparams-encode:{},uid:{},source:{}", cxbparam,
		                cxbparamEncode, uid, source);

		        /**
		         * 如果cxbparam或者是cxbparamEncode不为空的话，进行车享宝参数的解析
		         * 如果cxbparam 与 cxbparamEncode 同时存在，以cxbparam为真实参数
		         */
		        if (StringUtils.isNotBlank(cxbparam) || StringUtils.isNotBlank(cxbparamEncode)) {
		            if (StringUtils.isBlank(cxbparam) && StringUtils.isNotBlank(cxbparamEncode)) {
		                cxbparam = URLDecoder.decode(cxbparamEncode, "utf-8");
		            }

		            Map<String, String> params = FrameworkUtil.splitURLParams(cxbparam);
		            uid = params.get("uid");
		            source = params.get("source");
		            LOGGER.info("CXBUrlSignVerifHandle|first decryptData uid:{},source:{}", uid, source);
		        }

		        if (StringUtils.isNotBlank(uid)) {
		            String userId = AESUtils.decryptData(uid);
		            request.setAttribute(Constants.USER_CXB_USERID, userId);
		            LOGGER.info("CXBUrlSignVerifHandle|decryptData uid:{},userId:{}", uid, userId);
		        }

		        // 向control 里注入source， 设置source的值，默认source = 7（为微信），如果source=2为车享宝
		        request.setAttribute("source", GLOBAL_BY.CHEXIANG_BAO.getCode());

			/**
			 * 判断当前的source是车享宝,是拿到cxbparam ，如果不存在，就拿当前的getQueryString
			 * 设置cxbParameter到setAttribute中
			 */
			String cxbParameter = cxbparam;
			if (StringUtils.isBlank(cxbparam)) {
				cxbParameter = request.getQueryString();
			}

			String appVersion = request.getHeader(Constants.CXB_APP_APPVERSION);
			String cxbParameterEncode = URLEncoder
					.encode(cxbParameter, "utf-8");
			LOGGER.info(
					"CXBUrlSignVerifHandle|cxbParameter:{},cxbParameterEncode:{},appVersion:{}",
					cxbParameter, cxbParameterEncode, appVersion);
			request.setAttribute(Constants.USER_CXB_PARAMS, cxbParameterEncode);
			request.setAttribute(Constants.USER_CXB_APPVERSION, appVersion);
			
			return FrameworkUtil.stringToLong(uid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ApplicationChannel getApplicationChannel() {
		return ApplicationChannel.CXB;
	}

	public void gotoLogin(HttpServletRequest request,
			HttpServletResponse response,String callbackUrl) {
		LOGGER.info("CXBUrlSignVerifHandle.gotoLogin|url={}","/page/error_page_user.html");
		FrameworkUtil.writeErrorOut(response, "/page/error_page_user.html",null);
	}

    @Override
    public String getDefaultCallBackUrl(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

}
