package vin.suki.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "user")
public class User implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 用户昵称
	 */
	private String username;

	/**
	 * 用户昵称
	 */
	private String email;

	/**
	 * 账号
	 */
	private String userAccount;

	/**
	 * 账号状态（0- 正常 1- 封号）
	 */
	private Integer status;

	/**
	 * 邀请码
	 */
	private String invitationCode;

	/**
	 * 访问密钥
	 */
	private String accessKey;

	/**
	 * 钱包余额（分）
	 */
	private Integer balance;

	/**
	 * 安全密钥
	 */
	private String secretKey;

	/**
	 * 用户头像
	 */
	private String userAvatar;

	/**
	 * 性别
	 */
	private String gender;

	/**
	 * 用户角色: user, admin
	 */
	private String userRole;

	/**
	 * 密码
	 */
	private String userPassword;

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