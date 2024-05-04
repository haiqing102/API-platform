package edu.cqupt.apicommon.model.dto.productinfo;

import edu.cqupt.apicommon.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoQueryRequest extends PageRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 增加积分个数
	 */
	private Integer addPoints;


	/**
	 * 产品描述
	 */
	private String description;


	/**
	 * 金额(分)
	 */
	private Integer total;

	/**
	 * 产品类型（VIP-会员 RECHARGE-充值）
	 */
	private String productType;

}