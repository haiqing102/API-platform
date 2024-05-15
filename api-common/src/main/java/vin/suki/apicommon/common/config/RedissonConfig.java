package vin.suki.apicommon.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

	private String host;

	private String port;

	private Integer database;

	private String password;

	@Bean
	public RedissonClient redissonClient() {
		// 1.创建配置
		Config config = new Config();
		config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port)).setPassword(password).setDatabase(database);
		// 2.创建实例
		return Redisson.create(config);
	}

}
