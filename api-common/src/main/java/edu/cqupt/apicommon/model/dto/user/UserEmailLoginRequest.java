package edu.cqupt.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserEmailLoginRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;

	private String captcha;

}
