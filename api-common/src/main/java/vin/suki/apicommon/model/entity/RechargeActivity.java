package vin.suki.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "recharge_activity")
public class RechargeActivity implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 微信订单号/支付宝订单id
	 */
	private String orderNo;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 商品id
	 */
	private Long productId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除
	 */
	@TableLogic
	private Integer isDelete;

}