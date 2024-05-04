package edu.cqupt.apicommon.model.dto.interfaceinfo;

import lombok.Data;

@Data
public class RequestParamsField {

	private String id;

	private String fieldName;

	private String type;

	private String desc;

	private String required;

}