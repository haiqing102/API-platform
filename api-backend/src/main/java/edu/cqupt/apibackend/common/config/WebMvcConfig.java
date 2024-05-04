package edu.cqupt.apibackend.common.config;

import edu.cqupt.apibackend.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor())
				.addPathPatterns("/**")// 拦截所有请求
				.excludePathPatterns("/user/getCaptcha")// 不拦截的 url 地址
				.excludePathPatterns("/user/register")
				.excludePathPatterns("/user/login")
				.excludePathPatterns("/user/email/register")
				.excludePathPatterns("/user/email/login")
				.excludePathPatterns("/user/get/login")
				.excludePathPatterns("/user/get/invitationCode")
				.excludePathPatterns("/interfaceInfo/list/page")
				.excludePathPatterns("/interfaceInfo/get")
				.excludePathPatterns("/interfaceInfo/get/searchText");
	}

}