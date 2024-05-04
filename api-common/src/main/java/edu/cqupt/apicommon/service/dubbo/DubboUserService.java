package edu.cqupt.apicommon.service.dubbo;

import edu.cqupt.apicommon.model.vo.UserVo;

public interface DubboUserService {

	/**
	 * 通过访问密钥获取invoke用户
	 */
	UserVo getInvokeUserByAccessKey(String accessKey);

}
