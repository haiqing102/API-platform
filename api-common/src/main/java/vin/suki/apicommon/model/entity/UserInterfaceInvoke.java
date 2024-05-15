package vin.suki.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "user_interface_invoke")
public class UserInterfaceInvoke implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 调用人id
	 */
	private Long userId;

	/**
	 * 接口id
	 */
	private Long interfaceId;

	/**
	 * 总调用次数
	 */
	private Long totalInvokes;

	/**
	 * 调用状态（0-正常 1-封号）
	 */
	private Integer status;

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