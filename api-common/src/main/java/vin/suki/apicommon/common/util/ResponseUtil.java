package vin.suki.apicommon.common.util;

import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.response.BaseResponse;

/**
 * 响应结果工具类
 */
public class ResponseUtil {

	/**
	 * 成功
	 */
	public static <T> BaseResponse<T> success(T data) {
		return new BaseResponse<>(ResponseCode.SUCCESS.getCode(), data, "OK");
	}

	/**
	 * 错误
	 */
	public static <T> BaseResponse<T> error(ResponseCode responseCode) {
		return new BaseResponse<>(responseCode.getCode(), null, responseCode.getMessage());
	}

	/**
	 * 错误
	 */
	public static <T> BaseResponse<T> error(int code, String message) {
		return new BaseResponse<>(code, null, message);
	}

	/**
	 * 错误
	 */
	public static <T> BaseResponse<T> error(ResponseCode responseCode, T data, String message) {
		return new BaseResponse<>(responseCode.getCode(), data, message);
	}

	/**
	 * 错误
	 */
	public static <T> BaseResponse<T> error(ResponseCode responseCode, String message) {
		return new BaseResponse<>(responseCode.getCode(), null, message);
	}

}
