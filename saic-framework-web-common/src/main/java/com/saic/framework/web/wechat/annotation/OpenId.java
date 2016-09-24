package com.saic.framework.web.wechat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 与WXUseinfo annotation一起使用， 框架会自动把openid赋给用该annotation标识的controller入参中。  指定 @Openid String openid
annotation注解范围：parameter

 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author wangjiayan
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public @interface OpenId {

	String value() default "";
}
