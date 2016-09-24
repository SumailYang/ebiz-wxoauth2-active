package com.saic.framework.web.wechat.util;


public class DataUtils {
    public static final ThreadLocal<Long> threadUserIdData = new ThreadLocal<Long>();
    
    public static final ThreadLocal<String> threadSourceData = new ThreadLocal<String>();
 
    public static final ThreadLocal<String> threadOpenIdDate = new ThreadLocal<String>();
    
    public static final ThreadLocal<String> threadInvalidSessionDate = new ThreadLocal<String>();
    
}
