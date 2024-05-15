package vin.suki.apicommon.model.dto.interfaceinfo;


import lombok.Data;

import java.io.Serializable;

@Data
public class MusicDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否获取成功
	 */
	private Boolean success;

	/**
	 * 音乐榜单分类
	 */
	private String sort;

	/**
	 * 音乐信息
	 */
	private MusicInfo info;

}
