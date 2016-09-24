package com.saic.framework.web.wechat.popular.util;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class SignatureUtil {

    /**
     * 生成 paySign
     * 
     * @param map
     * @param paternerKey
     * @return
     */
    public static String generatePaySign(Map<String, String> map, String paySignKey) {
        if (paySignKey != null) {
            map.put("appkey", paySignKey);
        }
        Map<String, String> tmap = MapUtil.order(map);
        String str = MapUtil.mapJoin(tmap, true, false);
        return DigestUtils.shaHex(str);
    }

}
