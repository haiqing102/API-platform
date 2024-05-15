package vin.suki.apicommon.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MusicInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 音乐id
	 */
	private Long id;

	/**
	 * 歌名
	 */
	private String name;

	/**
	 * 作者
	 */
	private String auther;

	/**
	 * 音乐图片地址
	 */
	private String picUrl;

	/**
	 * 音乐地址
	 */
	private String url;

}
