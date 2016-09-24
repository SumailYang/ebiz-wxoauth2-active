package com.saic.framework.web.wechat.popular.bean;

import java.util.List;

public class CallBackIp extends BaseResult {

	private List<String> ip_list;

	public List<String> getIp_list() {
		return ip_list;
	}

	public void setIp_list(List<String> ip_list) {
		this.ip_list = ip_list;
	}
}