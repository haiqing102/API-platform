package edu.cqupt.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserBindEmailRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String emailAccount;

	private String captcha;

}
