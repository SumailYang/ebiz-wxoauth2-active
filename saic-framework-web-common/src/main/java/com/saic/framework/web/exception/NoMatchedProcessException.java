package com.saic.framework.web.exception;

public class NoMatchedProcessException extends RuntimeException {
	
	private static final long serialVersionUID = -1674672613187399552L;

	public NoMatchedProcessException( String errmsg) {
        super(String.format("%s", errmsg));
    }
}
