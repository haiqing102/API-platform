package edu.cqupt.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "payment_info")
public class PaymentInfo implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 商户订单号
	 */
	private String orderNo;

	/**
	 * 微信支付订单号
	 */
	private String transactionId;

	/**
	 * 交易类型
	 */
	private String tradeType;

	/**
	 * 交易状态(SUCCESS：支付成功
	 * REFUND：转入退款
	 * NOT_PAY：未支付
	 * CLOSED：已关闭
	 * REVOKED：已撤销（仅付款码支付会返回）
	 * USER_PAYING：用户支付中（仅付款码支付会返回）
	 * PAY_ERROR：支付失败（仅付款码支付会返回）)
	 */
	private String tradeState;

	/**
	 * 交易状态描述
	 */
	private String tradeStateDesc;

	/**
	 * 用户标识
	 */
	private String openid;

	/**
	 * 用户支付金额
	 */
	private Integer payerTotal;

	/**
	 * 货币类型
	 */
	private String currency;

	/**
	 * 用户支付币种
	 */
	private String payerCurrency;

	/**
	 * 接口返回内容
	 */
	private String content;

	/**
	 * 总金额(分)
	 */
	private Integer total;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 支付完成时间
	 */
	private String successTime;

}