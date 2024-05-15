package vin.suki.apibackend.service.impl.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.entity.User;
import vin.suki.apicommon.model.vo.UserVo;
import vin.suki.apicommon.service.dubbo.DubboUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

@DubboService
public class DubboUserServiceImpl implements DubboUserService {
	@Resource
	private UserService userService;

	@Override
	public UserVo getInvokeUserByAccessKey(String accessKey) {
		if (StringUtils.isAnyBlank(accessKey)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
		userLambdaQueryWrapper.eq(User::getAccessKey, accessKey);
		User user = userService.getOne(userLambdaQueryWrapper);
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(user, userVo);
		return userVo;
	}
}
