package vin.suki.apicommon.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InvokeRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private List<Field> requestParams;

	private String userRequestParams;

	@Data
	public static class Field {

		private String fieldName;

		private String value;

	}

}

