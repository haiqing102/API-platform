package edu.cqupt.apicommon.model.dto.pay;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayCreateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 接口id
	 */
	private String productId;


	/**
	 * 支付类型
	 */
	private String payType;

}