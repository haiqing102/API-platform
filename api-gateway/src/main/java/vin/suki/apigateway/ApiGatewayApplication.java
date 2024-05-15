package vin.suki.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;

import java.io.File;

@Slf4j
@EnableDubbo
@SpringBootApplication(
		exclude = {DataSourceAutoConfiguration.class,},
		scanBasePackages = {"vin.suki.apigateway", "vin.suki.apicommon.common"}
)
public class ApiGatewayApplication {

	public static void main(String[] args) {

		// 解决Dubbo本地启动多个应用的缓存冲突问题
		SpringApplication application = new SpringApplication(ApiGatewayApplication.class);
		// 1.添加自定义的ApplicationContextInitializer
		application.addInitializers(context -> {
			// 2.获取Environment对象
			Environment env = context.getEnvironment();
			// 3.从Environment中读取"spring.application.name"属性值
			String appName = env.getProperty("spring.application.name");
			String filePath = System.getProperty("user.home") + File.separator + ".dubbo" + File.separator + appName;
			// 4.修改dubbo的本地缓存路径，避免缓存冲突
			System.setProperty("dubbo.meta.cache.filePath", filePath);
			System.setProperty("dubbo.mapping.cache.filePath", filePath);
		});
		// 5.启动应用
		application.run(args);
		log.info("api-gateway启动成功...");
	}

}
