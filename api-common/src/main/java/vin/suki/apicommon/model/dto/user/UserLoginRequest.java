package vin.suki.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userAccount;

	private String userPassword;

}
