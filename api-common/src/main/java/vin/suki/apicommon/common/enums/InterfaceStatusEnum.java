package vin.suki.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口状态枚举
 */
@Getter
@AllArgsConstructor
public enum InterfaceStatusEnum {

	ONLINE("开启", 1),

	OFFLINE("关闭", 2),

	AUDITING("审核中", 0);

	private final String text;

	private final int value;

	/**
	 * 获取值列表
	 */
	public static List<Integer> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

}
