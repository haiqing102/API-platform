package edu.cqupt.apicommon.model.dto.user;

import edu.cqupt.apicommon.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest implements Serializable {

	private static final long serialVersionUID = 1L;


	private Long id;
	/**
	 * 用户昵称
	 */
	private String username;
	/**
	 * 账号
	 */
	private String userAccount;

	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 用户角色: user, admin
	 */
	private String userRole;
}