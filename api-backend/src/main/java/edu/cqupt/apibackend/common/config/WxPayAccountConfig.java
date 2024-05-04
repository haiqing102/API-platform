package edu.cqupt.apibackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayAccountConfig {
	/**
	 * 设置微信公众号或者小程序等的appid
	 */
	private String appId;

	/**
	 * 微信支付商户号
	 */
	private String mchId;

	/**
	 * api v3关键
	 */
	private String apiV3Key;

	/**
	 * 私钥路径
	 */
	private String privateKeyPath;

	/**
	 * 私人证书路径
	 */
	private String privateCertPath;

	/**
	 * 通知地址
	 */
	private String notifyUrl;

}