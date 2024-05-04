package edu.cqupt.apiinterface.util;

import cn.hutool.http.HttpRequest;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class RequestUtil {

	/**
	 * 生成url
	 *
	 * @param baseUrl 基本url
	 * @param params  params
	 * @return {@link String}
	 * @throws BusinessException api异常
	 */
	public static <T> String buildUrl(String baseUrl, T params) throws BusinessException {
		StringBuilder url = new StringBuilder(baseUrl);
		Field[] fields = params.getClass().getDeclaredFields();
		boolean isFirstParam = true;
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			// 跳过serialVersionUID属性
			if ("serialVersionUID".equals(name)) {
				continue;
			}
			try {
				Object value = field.get(params);
				if (value != null) {
					if (isFirstParam) {
						url.append("?").append(name).append("=").append(value);
						isFirstParam = false;
					} else {
						url.append("&").append(name).append("=").append(value);
					}
				}
			} catch (IllegalAccessException e) {
				throw new BusinessException(ResponseCode.OPERATION_ERROR, "构建url异常");
			}
		}
		return url.toString();
	}

	/**
	 * get请求
	 *
	 * @param baseUrl 基本url
	 * @param params  params
	 * @return {@link String}
	 * @throws BusinessException api异常
	 */
	public static <T> String get(String baseUrl, T params) throws BusinessException {
		return get(buildUrl(baseUrl, params));
	}

	/**
	 * get请求
	 *
	 * @param url url
	 * @return {@link String}
	 */
	public static String get(String url) {
		String body = HttpRequest.get(url).execute().body();
		log.info("【Interface】请求URL：{}，响应数据：{}", url, body);
		return body;
	}

}
