package com.saic.framework.web.wechat.popular.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsUtil {

	/**
	 * 生成 config接口 signature
	 * @param noncestr
	 * @param jsapi_ticket
	 * @param timestamp
	 * @param url
	 * @return
	 */
	public static String generateConfigSignature(String noncestr,String jsapi_ticket,String timestamp,String url){
		Map<String, String> map = new HashMap<String, String>();
		map.put("noncestr", noncestr);
		map.put("jsapi_ticket", jsapi_ticket);
		map.put("timestamp", timestamp);
		map.put("url", url);
		return SignatureUtil.generatePaySign(map, null);
	}

	/**
	 * 生成 config接口注入权限验证 JSON
	 * @param jsapi_ticket
	 * @param debug
	 * @param appId
	 * @param url
	 * @param jsApiList
	 *  onMenuShareTimeline
		onMenuShareAppMessage
		onMenuShareQQ
		onMenuShareWeibo
		startRecord
		stopRecord
		onVoiceRecordEnd
		playVoice
		pauseVoice
		stopVoice
		onVoicePlayEnd
		uploadVoice
		downloadVoice
		chooseImage
		previewImage
		uploadImage
		downloadImage
		translateVoice
		getNetworkType
		openLocation
		getLocation
		hideOptionMenu
		showOptionMenu
		hideMenuItems
		showMenuItems
		hideAllNonBaseMenuItem
		showAllNonBaseMenuItem
		closeWindow
		scanQRCode
		chooseWXPay
		openProductSpecificView
		addCard
		chooseCard
		openCard
	 * @return
	 */
	public static String generateConfigJson(String jsapi_ticket,boolean debug,String appId,String url,String... jsApiList){
		long timestamp = System.currentTimeMillis()/1000;
		String nonceStr = UUID.randomUUID().toString();
		String signature = generateConfigSignature(nonceStr, jsapi_ticket,timestamp+"",url);
		return new StringBuilder()
			.append("{")
			.append("debug:").append(debug).append(",")
			.append("appId:").append("'").append(appId).append("'").append(",")
			.append("timestamp:").append(timestamp).append(",")
			.append("nonceStr:").append("'").append(nonceStr).append("'").append(",")
			.append("signature:").append("'").append(signature).append("'").append(",")
			.append("jsApiList:").append(JsonUtil.toJSONString(jsApiList))
			.append("}").toString();
	}

	
}
