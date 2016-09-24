package com.saic.framework.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 验证签名注解
 * 
 * 由此注解判断是否需要对全参数做签名校验
 * 
 * @author zhangtisu
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifySignature {


}