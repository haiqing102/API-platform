package vin.suki.apicommon.model.vo;

import vin.suki.apicommon.model.entity.ProductInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProductOrderVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 订单编号
	 */
	private String orderNo;

	/**
	 * 支付类型
	 */
	private String payType;

	/**
	 * 过期时间
	 */
	private Date expirationTime;

	/**
	 * 商品名称
	 */
	private String orderName;

	/**
	 * 支付二维码地址
	 */
	private String codeUrl;


	/**
	 * 商品id
	 */
	private Long productId;

	/**
	 * 金额(分)
	 */
	private String total;

	/**
	 * 接口订单状态(SUCCESS：支付成功
	 * REFUND：转入退款
	 * NOT_PAY：未支付
	 * CLOSED：已关闭
	 * REVOKED：已撤销（仅付款码支付会返回）
	 * USER_PAYING：用户支付中（仅付款码支付会返回）
	 * PAY_ERROR：支付失败（仅付款码支付会返回）)
	 */
	private String status;

	/**
	 * 产品信息
	 */
	private ProductInfo productInfo;

	/**
	 * 增加积分个数
	 */
	private Integer addPoints;

	/**
	 * 支付宝订单体
	 */
	private String formData;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 产品描述
	 */
	private String description;

	/**
	 * 产品类型（VIP-会员 RECHARGE-充值）
	 */
	private String productType;

}
