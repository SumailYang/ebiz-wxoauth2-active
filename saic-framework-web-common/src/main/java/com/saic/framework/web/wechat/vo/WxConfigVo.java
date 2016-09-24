package com.saic.framework.web.wechat.vo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
wx.config({
    debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    appId: '', // 必填，公众号的唯一标识
    timestamp: , // 必填，生成签名的时间戳
    nonceStr: '', // 必填，生成签名的随机串
    signature: '',// 必填，签名，见附录1
    jsApiList: [] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
});
 * </pre>
 * 
 * @author zhaohuiliang
 *
 */
public class WxConfigVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3862700575047039089L;

	// 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	private Boolean debug = false;

	// 必填，公众号的唯一标识
	private String appId;

	private String timestamp;

	private String nonceStr;

	private String signature;

	private String jsApiList;

	public WxConfigVo(Boolean debug, String appId, String timestamp, String nonceStr, String signature,
			String jsApiList) {
		super();
		this.debug = debug;
		this.appId = appId;
		this.timestamp = timestamp;
		this.nonceStr = nonceStr;
		this.signature = signature;
		this.jsApiList = jsApiList;
	}

	/**
	 * 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，
	 * 仅在pc端时才会打印。
	 * 
	 * @return
	 */
	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	/**
	 * 必填，公众号的唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 必填，生成签名的时间戳
	 * 
	 * @return
	 */
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 必填，生成签名的随机串
	 * 
	 * @return
	 */
	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	/**
	 * 
	 * <h2>签名算法</h2> 签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket,
	 * timestamp（时间戳）, url（当前网页的URL，不包含#及其后面部分） 。 对所有待签名参数按照字段名的ASCII
	 * 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的
	 * 是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
	 * 
	 * <h3>即signature=sha1(string1)。 示例：</h3>
	 * <ul>
	 * <li>noncestr=Wm3WZYTPz0wzccnW</li>
	 * <li>jsapi_ticket=
	 * sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-
	 * HhTdfl2fzFy1AOcHKP7qg</li>
	 * <li>timestamp=1414587457</li>
	 * <li>url=http://mp.weixin.qq.com?params=value</li>
	 * </ul>
	 * 
	 * <ol>
	 * <li>步骤1. 对所有待签名参数按照字段名的ASCII
	 * 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1：
	 * 
	 * <pre>
	 * jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-
	 * HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=
	 * </pre>
	 * 
	 * </li> http://mp.weixin.qq.com?params=value
	 * <li>步骤2. 对string1进行sha1签名，得到signature：
	 * 
	 * <pre>
	 * 0f9de62fce790f9a083d5c99e95740ceb90c27ed
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * <h3>注意事项</h3>
	 * <ol>
	 * <li>签名用的noncestr和timestamp必须与wx.config中的nonceStr和timestamp相同。</li>
	 * <li>签名用的url必须是调用JS接口页面的完整URL。 出于安全考虑，开发者必须在服务器端实现签名的逻辑。</li>
	 * <li>如出现invalid signature 等错误详见附录5常见错误及解决办法。</li>
	 * </ol>
	 * 
	 * 
	 * @return
	 */
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	 * 
	 * <ul>
	 * <li>onMenuShareTimeline</li>
	 * <li>onMenuShareAppMessage</li>
	 * <li>onMenuShareQQ</li>
	 * <li>onMenuShareWeibo</li>
	 * <li>onMenuShareQZone</li>
	 * <li>startRecord</li>
	 * <li>stopRecord</li>
	 * <li>onVoiceRecordEnd</li>
	 * <li>playVoice</li>
	 * <li>pauseVoice</li>
	 * <li>stopVoice</li>
	 * <li>onVoicePlayEnd</li>
	 * <li>uploadVoice</li>
	 * <li>downloadVoice</li>
	 * <li>chooseImage</li>
	 * <li>previewImage</li>
	 * <li>uploadImage</li>
	 * <li>downloadImage</li>
	 * <li>translateVoice</li>
	 * <li>getNetworkType</li>
	 * <li>openLocation</li>
	 * <li>getLocation</li>
	 * <li>hideOptionMenu</li>
	 * <li>showOptionMenu</li>
	 * <li>hideMenuItems</li>
	 * <li>showMenuItems</li>
	 * <li>hideAllNonBaseMenuItem</li>
	 * <li>showAllNonBaseMenuItem</li>
	 * <li>closeWindow</li>
	 * <li>scanQRCode</li>
	 * <li>chooseWXPay</li>
	 * <li>openProductSpecificView</li>
	 * <li>addCard</li>
	 * <li>chooseCard</li>
	 * <li>openCard</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getJsApiList() {
		if (StringUtils.isBlank(jsApiList) || StringUtils.equals("null", jsApiList.trim())) {
			jsApiList = "'onMenuShareTimeline','onMenuShareAppMessage','onMenuShareQQ','onMenuShareWeibo',"
					+ "'onMenuShareQZone','startRecord','stopRecord','onVoiceRecordEnd','playVoice','pauseVoice',"
					+ "'stopVoice','onVoicePlayEnd','uploadVoice','downloadVoice','chooseImage','previewImage','uploadImage',"
					+ "'downloadImage','translateVoice','getNetworkType','openLocation','getLocation','hideOptionMenu',"
					+ "'showOptionMenu','hideMenuItems','showMenuItems','hideAllNonBaseMenuItem','showAllNonBaseMenuItem',"
					+ "'closeWindow','scanQRCode','chooseWXPay','openProductSpecificView','addCard','chooseCard','openCard'";
		}
		return jsApiList;
	}

	public void setJsApiList(String jsApiList) {
		this.jsApiList = jsApiList;
	}

	@Override
	public String toString() {
		return "WxConfigVo [debug=" + debug + ", appId=" + appId + ", timestamp=" + timestamp + ", nonceStr=" + nonceStr
				+ ", signature=" + signature + ", jsApiList=" + jsApiList + "]";
	}
}