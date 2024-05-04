package edu.cqupt.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserEmailRegisterRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String emailAccount;

	private String captcha;

	private String username;

	private String invitationCode;

	private String agreeToAnAgreement;

}
