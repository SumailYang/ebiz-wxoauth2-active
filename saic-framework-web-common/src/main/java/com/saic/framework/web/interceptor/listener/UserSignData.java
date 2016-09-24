package com.saic.framework.web.interceptor.listener;

public class UserSignData {
	public Long userId;
	public String channel;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		return "UserSignData [userId=" + userId + ", channel=" + channel + "]";
	}

	
}