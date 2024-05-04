package edu.cqupt.apibackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

	private String emailFrom = "3313634052@qq.com";

}
