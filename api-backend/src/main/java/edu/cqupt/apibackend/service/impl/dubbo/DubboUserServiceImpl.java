package edu.cqupt.apibackend.service.impl.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.cqupt.apibackend.service.UserService;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.model.entity.User;
import edu.cqupt.apicommon.model.vo.UserVo;
import edu.cqupt.apicommon.service.dubbo.DubboUserService;
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
