package edu.cqupt.apibackend;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@MapperScan("edu.cqupt.apibackend.mapper")
@EnableDubbo(scanBasePackages = "edu.cqupt.apibackend.service.impl.dubbo")
@SpringBootApplication(scanBasePackages = {"edu.cqupt.apibackend", "edu.cqupt.apicommon.common"})
public class ApiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiBackendApplication.class, args);
		log.info("api-backend启动成功...");
	}

}
