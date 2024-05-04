package edu.cqupt.apibackend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("!prod")
public class Knife4jConfig {

	@Bean
	public Docket defaultApi2() {
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(new ApiInfoBuilder()
						.title("api-backend")
						.description("api-backend接口文档")
						.version("1.0")
						.build())
				.select()
				// 指定Controller扫描包路径
				.apis(RequestHandlerSelectors.basePackage("edu.cqupt.apibackend.controller"))
				.paths(PathSelectors.any())
				.build();
	}

}
