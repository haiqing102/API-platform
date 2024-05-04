package edu.cqupt.apibackend.common.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@AllArgsConstructor
public class PayConfiguration {

	@Resource
	private WxPayAccountConfig properties;

	@Resource
	private AliPayConfig aliPayConfig;

	@Bean
	@ConditionalOnMissingBean
	public WxPayService wxService() {

		WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(StringUtils.trimToNull(this.properties.getAppId()));
		payConfig.setMchId(StringUtils.trimToNull(this.properties.getMchId()));
		payConfig.setApiV3Key(StringUtils.trimToNull(this.properties.getApiV3Key()));
		payConfig.setPrivateKeyPath(StringUtils.trimToNull(this.properties.getPrivateKeyPath()));
		payConfig.setPrivateCertPath(StringUtils.trimToNull(this.properties.getPrivateCertPath()));
		payConfig.setNotifyUrl(StringUtils.trimToNull(this.properties.getNotifyUrl()));

		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		WxPayService wxPayService = new WxPayServiceImpl();
		wxPayService.setConfig(payConfig);
		return wxPayService;
	}

	@Bean
	public void aliPayApi() {
		AliPayApiConfig aliPayApiConfig = AliPayApiConfig.builder()
				.setAppId(aliPayConfig.getAppId())
				.setAliPayPublicKey(aliPayConfig.getAliPayPublicKey())
				.setCharset("UTF-8")
				.setPrivateKey(aliPayConfig.getPrivateKey())
				// .setServiceUrl("https://openapi.alipay.com/gateway.do")
				.setServiceUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do")
				.setSignType("RSA2")
				.setCertModel(false)
				.build(); // 普通公钥方式
		AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
	}

}