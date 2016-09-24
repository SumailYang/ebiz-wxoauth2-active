package com.saic.framework.web.wechat.popular.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.saic.framework.web.wechat.popular.bean.CallBackIp;
import com.saic.framework.web.wechat.popular.bean.Token;
import com.saic.framework.web.wechat.popular.client.LocalHttpClient;

public class TokenAPI extends BaseAPI {

	/**
	 * 获取access_token
	 * 
	 * @param appid
	 * @param secret
	 * @return
	 */
	public static Token token(String appid, String secret) {
		HttpUriRequest httpUriRequest = RequestBuilder.post().setUri(BASE_URI + "/cgi-bin/token")
				.addParameter("grant_type", "client_credential").addParameter("appid", appid)
				.addParameter("secret", secret).build();
		return LocalHttpClient.executeJsonResult(httpUriRequest, Token.class);
	}

	public static CallBackIp getCallBackIp(String access_token) {
		HttpUriRequest httpUriRequest = RequestBuilder.post().setUri(BASE_URI + "/cgi-bin/getcallbackip")
				.addParameter("access_token", access_token).build();
		return LocalHttpClient.executeJsonResult(httpUriRequest, CallBackIp.class);
	}

}
