package edu.cqupt.apicommon.common.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 状态码
	 */
	private int code;

	/**
	 * 响应数据
	 */
	private T data;

	/**
	 * 响应消息
	 */
	private String message;

	public BaseResponse(int code, T data, String message) {
		this.code = code;
		this.data = data;
		this.message = message;
	}

}
