package com.saic.framework.web.wechat.popular.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.saic.framework.web.wechat.popular.bean.WXUser;
import com.saic.framework.web.wechat.popular.client.LocalHttpClient;

public class UserAPI extends BaseAPI{

	/**
	 * 获取用户基本信息
	 * @param access_token
	 * @param openid
	 * @return
	 */
	public static WXUser userInfo(String access_token,String openid){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setUri(BASE_URI+"/cgi-bin/user/info")
				.addParameter("access_token",access_token)
				.addParameter("openid",openid)
				.addParameter("lang","zh_CN")
				.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,WXUser.class);
	}
}
