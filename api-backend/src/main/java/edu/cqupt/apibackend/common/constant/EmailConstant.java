package edu.cqupt.apibackend.common.constant;

public interface EmailConstant {

	/**
	 * 电子邮件html内容路径 resources目录下
	 */
	String EMAIL_HTML_CONTENT_PATH = "/html/captcha.html";

	/**
	 * 电子邮件html支付成功路径
	 */
	String EMAIL_HTML_PAY_SUCCESS_PATH = "/html/purchase.html";

	/**
	 * captcha缓存键
	 */
	String CAPTCHA_CACHE_KEY = "api:captcha:";

	/**
	 * 电子邮件主题
	 */
	String EMAIL_SUBJECT = "验证码";

	/**
	 * 电子邮件标题
	 */
	String EMAIL_TITLE = "API 接口服务平台";

	/**
	 * 电子邮件标题英语
	 */
	String EMAIL_TITLE_ENGLISH = "API-Interface-Service-Platform";

	/**
	 * 平台负责人
	 */
	String PLATFORM_RESPONSIBLE_PERSON = "API 接口服务平台";

	/**
	 * 平台地址
	 */
	String PLATFORM_ADDRESS = "<a href='https://api.suki.vin'>请联系我们！</a>";

	String PATH_ADDRESS = "'https://api.suki.vin'";

}
