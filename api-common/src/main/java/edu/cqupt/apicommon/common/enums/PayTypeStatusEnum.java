package edu.cqupt.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PayTypeStatusEnum {

	WX("微信支付", "WX"),

	ALIPAY("支付宝支付", "ALIPAY");

	private final String text;

	private final String value;

	/**
	 * 获取值列表
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

}
