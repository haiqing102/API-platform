package edu.cqupt.apibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.cqupt.apicommon.model.entity.InterfaceInfo;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

	/**
	 * 校验
	 *
	 * @param add           是否为创建校验
	 * @param interfaceInfo 接口信息
	 */
	void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

	/**
	 * 更新总调用数
	 *
	 * @param interfaceId 接口id
	 * @return boolean
	 */
	boolean updateTotalInvokes(long interfaceId);
}
