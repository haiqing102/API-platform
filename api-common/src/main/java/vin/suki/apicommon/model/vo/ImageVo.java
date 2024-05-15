package vin.suki.apicommon.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uid;

	private String name;

	private String status;

	private String url;

}