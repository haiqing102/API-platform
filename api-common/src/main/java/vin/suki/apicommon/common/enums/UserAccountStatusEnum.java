package vin.suki.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum UserAccountStatusEnum {

	NORMAL("正常", 0),

	BAN("封禁", 1);

	private final String text;

	private final int value;

	/**
	 * 获取值列表
	 */
	public static List<Integer> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

}
