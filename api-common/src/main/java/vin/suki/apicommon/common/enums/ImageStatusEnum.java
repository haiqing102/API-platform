package vin.suki.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片状态枚举
 */
@Getter
@AllArgsConstructor
public enum ImageStatusEnum {

	SUCCESS("success", "成功"),

	ERROR("error", "失败");

	private final String status;

	private final String value;

}
