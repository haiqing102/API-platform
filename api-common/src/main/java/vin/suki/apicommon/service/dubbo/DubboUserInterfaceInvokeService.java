package vin.suki.apicommon.service.dubbo;

public interface DubboUserInterfaceInvokeService {

	/**
	 * 接口调用
	 *
	 * @param interfaceInfoId 接口信息id
	 * @param userId          用户id
	 * @param reduceScore     减少积分数
	 * @return boolean
	 */
	boolean invoke(Long interfaceInfoId, Long userId, Integer reduceScore);

}
