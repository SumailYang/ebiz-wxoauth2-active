package com.saic.framework.web.wechat.exception;

public class WechatAjaxNotSupporttedException extends Exception {
    private static final long serialVersionUID = -129402191773827658L;

    private String code="003";
    
    public WechatAjaxNotSupporttedException(String message) {
        super(message);
    }

	public WechatAjaxNotSupporttedException(String message, Throwable cause) {
        super(message, cause);
    }
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

    
}
