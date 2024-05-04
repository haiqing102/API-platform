package edu.cqupt.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

	SUCCESS("支付成功", "SUCCESS"),

	PAY_ERROR("支付失败", "PAY_ERROR"),

	USER_PAYING("用户支付中", "USER_PAYING"),

	CLOSED("已关闭", "CLOSED"),

	NOT_PAY("未支付", "NOT_PAY"),

	REFUND("转入退款", "REFUND"),

	PROCESSING("退款中", "PROCESSING"),

	REVOKED("已撤销（刷卡支付）", "REVOKED"),

	UNKNOWN("未知状态", "UNKNOWN");

	private final String text;

	private final String value;

	/**
	 * 获取值列表
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

}
