package vin.suki.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 */
@Getter
@AllArgsConstructor
public enum FileUploadBizEnum {

	USER_AVATAR("用户头像", "user_avatar"),

	INTERFACE_AVATAR("接口头像", "interface_avatar");

	private final String text;

	private final String value;

	/**
	 * 获取值列表
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}

	/**
	 * 根据value获取枚举
	 */
	public static FileUploadBizEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}

}
