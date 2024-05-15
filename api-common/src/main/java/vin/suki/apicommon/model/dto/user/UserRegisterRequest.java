package vin.suki.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userAccount;

	private String userPassword;

	private String username;

	private String checkPassword;

	private String invitationCode;

	private String agreeToAnAgreement;

}
