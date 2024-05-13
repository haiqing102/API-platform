package edu.cqupt.apicommon.common.exception;

import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.response.BaseResponse;
import edu.cqupt.apicommon.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 处理业务异常
	 */
	@ExceptionHandler(BusinessException.class)
	public BaseResponse<?> businessExceptionHandler(BusinessException e) {
		log.error("BusinessException: " +e.getMessage());
		return ResponseUtil.error(e.getCode(), e.getMessage());
	}

	/**
	 * 处理其他异常
	 */
	@ExceptionHandler(Exception.class)
	public BaseResponse<?> runtimeExceptionHandler(Exception e) {
		log.error("Exception：" + e.getMessage());
		return ResponseUtil.error(ResponseCode.SYSTEM_ERROR, e.getMessage());
	}

}
