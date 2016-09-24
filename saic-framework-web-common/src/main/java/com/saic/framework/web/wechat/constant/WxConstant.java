/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: WxConstant.java
 * Author:   zhaohuiliang
 * Date:     2015年9月22日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.constant;

import org.apache.commons.lang.StringUtils;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
public class WxConstant {

	public static String SESSION_SAIC_WX_OPENID = "SESSION_SAIC_WX_OPENID";
	
	public static String SESSION_SAIC_APPLICATIONCHANNEL = "SESSION_SAIC_APPLICATIONCHANNEL_ID";

	public static String SESSION_SAIC_CX_ULOGIN_TOKEN = "SESSION_SAIC_CX_ULOGIN_TOKEN";

	public static String SESSION_SAIC_CX_USERID = "SESSION_SAIC_CX_USERID";
	
	public static String SESSION_CREATE_TIME_KEY="SESSION_SAIC_CX_USERID_Time";
	
	public static String SESSION_SSO_CX_USERID ="";

	public static String BROWSER_CHANNEL_SOURCE_WEIXIN = "weixin";

	public static String BROWSER_CHANNEL_SOURCE_CXB = "chexiangbao";

	public static String SESSION_SAIC_CX_USER = "SESSION_SAIC_CX_USER";

	public static String TICKET_KEY_PREFIX = "JSAPITOKEN_01";

	public static String TOKEN_KEY_PREFIX = "gh_72fe40f76a52";

	public static String SAIC_WX_APPID = "wxc0c5978849e779cc";

	public static String SAIC_WX_USER_KEY = "wxuser_";

	public static String SAIC_WX_APPSECRET = "47eeaeeb35a8c182504822d73a482138";

	public static String SAIC_EBIZ_WXOAUTH_JUMP_URL_ADDRESS = "http://wx.chexiang.com/wxoauth2/wxroutine?backurl={backurl}";

	public static String SAIC_EBIZ_WXOAUTH_JUMP_STATUS = "1";

	public static String JUMP_STATUS_TRUE = "1";

	public static String JUMP_STATUS_FALSE = "0";

	public static String SAIC_BASE_URL = "http://wxrtnact.chexiang.com";

	public static String SAIC_BASE_URL_WITH_SUFFIX = "http://wxrtnact.chexiang.com/wxoauth2";

	public static String SAIC_USERLOGIN_URL = "http://m.dds.com/account/wxlogin.htm?fromType=1&token={token}&backUrl={backUrl}";

	public static Boolean SAIC_MMS_CX_PROJECT_WXOAUTH_ISTEST = true;

	public static Boolean SAIC_MMS_CX_PROJECT_USERLOGIN_ISTEST = true;

	public static long SAIC_WX_TICKET_ROTARY_PERIOD = 10000;

	public static int SAIC_WX_USER_REDIS_TIME = 600;

	public static long SAIC_WX_TOKEN_ROTARY_PERIOD = 10000;

	public static int SAIC_WX_TOKEN_TIMING_REFRESH_TIME = 60 * 118;
	
	public static String SAIC_WX_NAMESAPCE ="MEMBERWEIXIN";

	public static boolean isTestEnable;
	
	public static String TEST_FLAG_ATTRIBUTE_IN_SESSION="isTest";
	
	/**
	 * 0，表示未登录 1.表示已登录
	 */
	public static String SAIC_CX_USER_LOGIN_STATUS_KEY = "SAIC_CX_USER_LOGIN_STATUS_KEY_";

	/**
	 * 取消关注，中间状态，需要进行登录
	 */
	public static String SAIC_CX_USER_LOGIN_STATUS_NOLOGIN = "0";

	/**
	 * 已登录
	 */
	public static String SAIC_CX_USER_LOGIN_STATUS_LOGIN = "1";

	/**
	 * js-sdk-api：时间戳
	 */
	public static String JS_TIMESTAMP = "1421720667";

	/**
	 * js-sdk-api：随机串
	 */
	public static String JS_NONCE_STR = "82693e11-b9bc-558e-892f-f5289f46cd0w";

	public static int REDISTIME = 1800;

	/**
	 * 设置微信授权测试（true|false）
	 * 
	 * @param cxProjectWXOauthIsTest
	 */
	public void setCXProjectWXOauthIsTest(Boolean cxProjectWXOauthIsTest) {
		if (cxProjectWXOauthIsTest != null) {
			WxConstant.SAIC_MMS_CX_PROJECT_WXOAUTH_ISTEST = cxProjectWXOauthIsTest;
		}
	}

	/**
	 * 设置用户登录测试（true|false）
	 * 
	 * @param cxProjectUserLoginIsTest
	 */
	public void setCXProjectUserLoginIsTest(Boolean cxProjectUserLoginIsTest) {
		if (cxProjectUserLoginIsTest != null) {
			WxConstant.SAIC_MMS_CX_PROJECT_USERLOGIN_ISTEST = cxProjectUserLoginIsTest;
		}
	}

	/**
	 * 设置微信的token定时刷新时间
	 * 
	 * @param wxTokenRedisKey
	 */
	public void setWxTokenTimingRefreshTime(Integer wxTokenTimingRefreshTime) {
		if (wxTokenTimingRefreshTime != null && wxTokenTimingRefreshTime > 0) {
			WxConstant.SAIC_WX_TOKEN_TIMING_REFRESH_TIME = wxTokenTimingRefreshTime;
		}
	}

	/**
	 * 设置微信的token key
	 * 
	 * @param wxTokenRedisKey
	 */
	public void setWxTokenRedisKey(String wxTokenRedisKey) {
		if (StringUtils.isNotBlank(wxTokenRedisKey)) {
			WxConstant.TOKEN_KEY_PREFIX = wxTokenRedisKey;
		}
	}

	/**
	 * 设置微信的ticket key
	 * 
	 * @param wxTicketRedisKey
	 */
	public void setWxTicketRedisKey(String wxTicketRedisKey) {
		if (StringUtils.isNotBlank(wxTicketRedisKey)) {
			WxConstant.TICKET_KEY_PREFIX = wxTicketRedisKey;
		}
	}

	public void setWxOpenIdSession(String wxOpenIdSession) {
		if (StringUtils.isNotBlank(wxOpenIdSession)) {
			WxConstant.SESSION_SAIC_WX_OPENID = wxOpenIdSession;
		}
	}

	public void setCxUserIdSession(String cxUserIdSession) {
		if (StringUtils.isNotBlank(cxUserIdSession)) {
			WxConstant.SESSION_SAIC_CX_USERID = cxUserIdSession;
		}
	}

	public void setCxUserKeySession(String cxUserKeySession) {
		if (StringUtils.isNotBlank(cxUserKeySession)) {
			WxConstant.SESSION_SAIC_CX_USER = cxUserKeySession;
		}
	}

	public void setWxAppId(String wxAppId) {
		if (StringUtils.isNotBlank(wxAppId)) {
			WxConstant.SAIC_WX_APPID = wxAppId;
		}
	}

	public void setWxAppSecret(String wxAppSecret) {
		if (StringUtils.isNotBlank(wxAppSecret)) {
			WxConstant.SAIC_WX_APPSECRET = wxAppSecret;
		}
	}

	public void setWxoauthJumpStatus(String wxoauthJumpStatus) {
		if (StringUtils.isNotBlank(wxoauthJumpStatus)) {
			WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_STATUS = wxoauthJumpStatus;
		}
	}

	/**
	 * 基础url地址
	 * 
	 * @param wxoauthJumpUrlAddress
	 */
	public void setWxoauthJumpUrlAddress(String wxoauthJumpUrlAddress) {
		if (StringUtils.isNotBlank(wxoauthJumpUrlAddress)) {
			WxConstant.SAIC_EBIZ_WXOAUTH_JUMP_URL_ADDRESS = wxoauthJumpUrlAddress;
		}
	}

	/**
	 * 不带后缀的baseUrl
	 * 
	 * @param visitBaseUrl
	 */
	public void setVisitBaseUrl(String visitBaseUrl) {
		if (StringUtils.isNotBlank(visitBaseUrl)) {
			WxConstant.SAIC_BASE_URL = visitBaseUrl;
		}
	}

	/**
	 * 带后缀的base url
	 * 
	 * @param visitBaseUrlWithSuffix
	 */
	public void setVisitBaseUrlWithSuffix(String visitBaseUrlWithSuffix) {
		if (StringUtils.isNotBlank(visitBaseUrlWithSuffix)) {
			WxConstant.SAIC_BASE_URL_WITH_SUFFIX = visitBaseUrlWithSuffix;
		}
	}

	public void setVisitUserLoginUrl(String visitUserLoginUrl) {
		if (StringUtils.isNotBlank(visitUserLoginUrl)) {
			WxConstant.SAIC_USERLOGIN_URL = visitUserLoginUrl;
		}
	}

	/**
	 * token 失效时间
	 * 
	 * @param tokenFailureTime
	 */
	public void setTokenFailureTime(Integer tokenFailureTime) {
		if (tokenFailureTime != null && tokenFailureTime > 0) {
			WxConstant.REDISTIME = tokenFailureTime;
		}
	}

	public static boolean isTestEnable() {
		return isTestEnable;
	}

	public void setTestEnable(boolean isTestEnable) {
		this.isTestEnable = isTestEnable;
	}
	
}