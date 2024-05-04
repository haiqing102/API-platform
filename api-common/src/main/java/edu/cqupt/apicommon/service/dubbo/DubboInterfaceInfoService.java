package edu.cqupt.apicommon.service.dubbo;


import edu.cqupt.apicommon.model.entity.InterfaceInfo;

public interface DubboInterfaceInfoService {

	/**
	 * 获取接口信息
	 *
	 * @param path   路径
	 * @param method 方法
	 * @return {@link InterfaceInfo}
	 */
	InterfaceInfo getInterfaceInfo(String path, String method);

}
