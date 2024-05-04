package edu.cqupt.apibackend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

	/**
	 * 其中任意一个角色
	 */
	String[] anyRole() default "";

	/**
	 * 必须为某个角色
	 */
	String mustRole() default "";

}

