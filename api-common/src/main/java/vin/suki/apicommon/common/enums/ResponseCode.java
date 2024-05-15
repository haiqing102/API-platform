package vin.suki.apicommon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

	/**
	 * 成功
	 */
	SUCCESS(0, "OK"),

	/**
	 * 请求参数错误
	 */
	PARAMS_ERROR(40000, "请求参数错误"),

	/**
	 * 账号已封禁
	 */
	PROHIBITED(40001, "账号已封禁"),

	/**
	 * 未登录
	 */
	NOT_LOGIN_ERROR(40100, "未登录"),

	/**
	 * 无权限
	 */
	NO_AUTH_ERROR(40101, "无权限"),

	/**
	 * 禁止访问
	 */
	FORBIDDEN_ERROR(40300, "禁止访问"),

	/**
	 * 请求数据不存在
	 */
	NOT_FOUND_ERROR(40400, "请求数据不存在"),

	/**
	 * 系统内部异常
	 */
	SYSTEM_ERROR(50000, "系统内部异常"),

	/**
	 * 操作失败
	 */
	OPERATION_ERROR(50001, "操作失败");

	/**
	 * 状态码
	 */
	private final int code;

	/**
	 * 状态码消息
	 */
	private final String message;

}
