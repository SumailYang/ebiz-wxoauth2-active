/*
 * Copyright (C), 2013-2015, 上海汽车集团有限公司
 * FileName: OAuthUtil.java
 * Author:   zhaohuiliang
 * Date:     2015年10月9日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.saic.framework.web.wechat.util;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.saic.framework.web.wechat.service.OAuthService;
import com.saic.framework.web.wechat.vo.WxConfigVo;
import com.saic.framework.web.wechat.constant.WxConstant;
import com.saic.framework.web.wechat.popular.api.SnsAPI;
import com.saic.framework.web.wechat.popular.bean.SnsToken;
import com.saic.framework.web.wechat.popular.bean.WXUser;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 */
@Component
public final class OAuthUtil {

    private static OAuthService oauthService;

    @Resource(name = "oAuthService")
    public void setOauthService(OAuthService oauthService) {
        OAuthUtil.oauthService = oauthService;
    }

    /**
     * 根据code获取openId
     * 
     * 微信网页授权，就是需要拿到openid，获取openid，需要微信授权，授权之后返回code和state，我们就可以根据code拿到openid了
     * 
     * @param code 网页授权code
     * @return
     */
    public static String getOpenId(String code) {
        return oauthService.getOpenId(code);
    }

    /**
     * *
     * <h2>签名算法</h2> 签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket, timestamp（时间戳）, url（当前网页的URL，不包含#及其后面部分） 。
     * 对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的
     * 是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
     * 
     * <h3>即signature=sha1(string1)。 示例：</h3>
     * <ul>
     * <li>noncestr=Wm3WZYTPz0wzccnW</li>
     * <li>jsapi_ticket= sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl- HhTdfl2fzFy1AOcHKP7qg</li>
     * <li>timestamp=1414587457</li>
     * <li>url=http://mp.weixin.qq.com?params=value</li>
     * </ul>
     * 
     * <ol>
     * <li>步骤1. 对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1：
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
     * @param url 当前访问的url地址
     * @return
     */
    public static String getSignature(String url) {
        return oauthService.getSignature(url);
    }

    /**
     * 把参数组合成json字符串输出
     * 
     * @param url 当前url地址
     * @param debug 调试模式
     * @param jsApiList 访问方法的列表
     * @return
     */
    public static String getWxConfigJson(String url, boolean debug, List<String> jsApiList) {
        return oauthService.getWxConfigJson(url, debug, jsApiList);
    }

    /**
     * 把参数组合成对象输出
     * 
     * @param url 当前url地址
     * @return
     */
    public static WxConfigVo getWxConfigJson(String url) {
        return oauthService.getWxConfigJson(url);
    }

    /**
     * 根绝tokenid存储到redis中，保证sso登录不失效
     * 
     * @param tokenId 微信这边使用openId作为token
     * @return
     */
    public static String getLoginToken(String tokenId) {
        return oauthService.getToken(tokenId);
    }

    /**
     * 根据openId获取wx user信息
     * 
     * @param openId
     * @return
     */
    public static WXUser getWxUserInfo(String openId) {
        return oauthService.getWxUserInfo(openId);
    }

    /**
     * 根据openId和网页授权access_token,获取wx user信息
     * 
     * @param openId
     * @return
     */
    public static WXUser getWxPageOAuthUserInfo(String openId, String access_token) {
        return oauthService.getWxUserInfo(openId, access_token);
    }
    
    public static WXUser getWechatUserInfo(String code){
        SnsToken token = SnsAPI.oauth2AccessToken(WxConstant.SAIC_WX_APPID, WxConstant.SAIC_WX_APPSECRET, code);
        String openId = token.getOpenid();
        String access_token = token.getAccess_token();
        return getWxPageOAuthUserInfo(openId,access_token);
    }
}
