package com.saic.framework.web.wechat.exception;

/**
 * 
 * 微信请求返回错误信息<br>
 * 〈功能详细描述〉
 *
 * @author zhaohuiliang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class WeiXinRequestBackException extends RuntimeException {

    /**
     */
    private static final long serialVersionUID = -129402191774827658L;

    public WeiXinRequestBackException(String errcode, String errmsg) {
        super(String.format("[%s],%s", errcode, errmsg));
    }
}
