package com.saic.framework.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.saic.framework.web.constant.ApplicationChannel;

/**
 *  是该多终端框架的最主要的annotation, 只有标示该SignonVerification的类或方法，该框架才会有效
 *  check 当前用户是否登录，
 *  没有登录是指，
 *    在车享汇微信服务号，既没有绑定
 *    在APP没有登录，也没有登录
 *    在M站，没有登录
 *  如果没有登录，
 *  1. 判断是否是Ajax, 如果是，返回json格式的错误信息.
 *  2. 如果不是
 *  并且redict2Login is true,   跳转到登录页面， 一般用于提交类
 *  如果redict2Login is false, 一般用于首页面渲染，得到当前用户是否登录
 *  3. 无论是否是Ajax还是redict2Login的值为何，如果当前用户已经登录，保证登录状态保持，在之后的controller 和 页面渲染都可得到。
 *  
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author wangjiayan
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SignonVerification {
        boolean redict2Login() default true;
        String  supportApplicationChannels() default "";
}
