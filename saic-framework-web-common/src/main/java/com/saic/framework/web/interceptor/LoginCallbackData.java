package com.saic.framework.web.interceptor;

import com.saic.framework.web.constant.ApplicationChannel;

public class LoginCallbackData {
     ApplicationChannel appChannel;
     
     String defaultURl;
    public ApplicationChannel getAppChannel() {
        return appChannel;
    }
    
    public void setAppChannel(ApplicationChannel appChannel) {
        this.appChannel = appChannel;
    }
    public String getDefaultURl() {
        return defaultURl;
    }
    
    public void setDefaultURl(String defaultURl) {
        this.defaultURl = defaultURl;
    }
     
}
