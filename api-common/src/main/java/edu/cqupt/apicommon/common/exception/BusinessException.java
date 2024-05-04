package edu.cqupt.apicommon.common.exception;

import edu.cqupt.apicommon.common.enums.ResponseCode;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

	/**
	 * 状态码
	 */
	private final int code;

	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}

	public BusinessException(ResponseCode responseCode) {
		super(responseCode.getMessage());
		this.code = responseCode.getCode();
	}

	public BusinessException(ResponseCode responseCode, String message) {
		super(message);
		this.code = responseCode.getCode();
	}

	public int getCode() {
		return code;
	}

}
