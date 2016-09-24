package com.saic.framework.web.wechat.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.saic.framework.web.constant.ApplicationChannel;

public class Test {
    public static void main(String[] args) {
//        for (int i = 0; i <  8; i++) {
//            Long userId = null;
//            Object userIdObj=getUserId(i);
//            if (userIdObj != null) {
//                if(userIdObj instanceof String){
//                    String userIdStr = (String)userIdObj;
//                    if (userIdStr != null && !"".equals(userIdStr) && !"-1".equals(userIdStr) && NumberUtils.isNumber(userIdStr)) {
//                        userId = Long.parseLong(userIdStr);                 
//                   }
//                }
//                if(userIdObj instanceof Long){
//                    userId = (Long)userIdObj;
//                }
//            }
//            
//            System.out.println("i="+i+";userId="+(userId==null?"null":userId.toString()));
//        }
        
//        Boolean flag = true;
//        if(null == flag || !flag){
//            System.out.println("aaaaaa");
//        }else{
//            System.out.println("bbbb");
//        }
//        String str1=ApplicationChannel.CXB+","+ApplicationChannel.WECHAT;
//       
//        String[] channelArr = new String[]{"PC","WECHAT","CXB","WAP","DEFAULT"};
//        String[] channelArr2 =new String[]{"PC端游览器","微信客户端","车享宝APP","WAP端游览器","WAP端游览器"};
//        for (int i = 0; i < channelArr2.length; i++) {
//            str1 = str1.replace(channelArr[i], channelArr2[i]);
//        }
//        System.out.println(str1);
        
        String str ="<h1 id='alertMsgH1' style='display:none'>当前页面不支持你所使用的设备,请使用{{spChannel}}设备打开</h1>";
        System.out.println(str.indexOf("{{spChannel}}"));
    }
    
    private static Object getUserId(int test) {
        if(test == 1){
            return "12";
        }else if (test ==2) {
            return "";
        }else if (test ==3) {
            return "-1";
        }else if (test==4) {
            return "str";
        }else if(test==5) {
            return 12L;
        }else if (test ==6) {
            return 1256L;
        }else if (test ==7) {
            return -123l;
        }
        
        return null;
    }
}
