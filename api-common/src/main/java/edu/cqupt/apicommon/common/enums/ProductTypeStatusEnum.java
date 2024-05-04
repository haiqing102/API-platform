package edu.cqupt.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ProductTypeStatusEnum {

	VIP("VIP会员", "VIP"),

	RECHARGE("余额充值", "RECHARGE"),

	RECHARGE_ACTIVITY("充值活动", "RECHARGE_ACTIVITY");

	private final String text;

	private final String value;

	/**
	 * 获取值列表
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

}
