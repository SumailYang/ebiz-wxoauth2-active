package com.saic.framework.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 
 * 用于入参，会把当前用户userid赋给该值
 *
 * @author wangjiayan
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public @interface UserId {
	String value() default "";
}
