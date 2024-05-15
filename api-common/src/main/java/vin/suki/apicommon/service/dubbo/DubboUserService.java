package vin.suki.apicommon.service.dubbo;

import vin.suki.apicommon.model.vo.UserVo;

public interface DubboUserService {

	/**
	 * 通过访问密钥获取invoke用户
	 */
	UserVo getInvokeUserByAccessKey(String accessKey);

}
