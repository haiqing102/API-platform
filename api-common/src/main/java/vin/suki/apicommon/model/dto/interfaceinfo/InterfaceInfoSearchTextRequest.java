package vin.suki.apicommon.model.dto.interfaceinfo;

import vin.suki.apicommon.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoSearchTextRequest extends PageRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String searchText;

}
