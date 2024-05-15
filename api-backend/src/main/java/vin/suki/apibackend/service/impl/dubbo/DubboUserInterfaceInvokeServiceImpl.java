package vin.suki.apibackend.service.impl.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import vin.suki.apibackend.mapper.UserInterfaceInvokeMapper;
import vin.suki.apibackend.service.InterfaceInfoService;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.entity.UserInterfaceInvoke;
import vin.suki.apicommon.service.dubbo.DubboUserInterfaceInvokeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@DubboService
public class DubboUserInterfaceInvokeServiceImpl extends ServiceImpl<UserInterfaceInvokeMapper, UserInterfaceInvoke>
		implements DubboUserInterfaceInvokeService {
	@Resource
	private InterfaceInfoService interfaceInfoService;
	@Resource
	private UserService userService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean invoke(Long interfaceInfoId, Long userId, Integer reduceScore) {
		LambdaQueryWrapper<UserInterfaceInvoke> invokeLambdaQueryWrapper = new LambdaQueryWrapper<>();
		invokeLambdaQueryWrapper.eq(UserInterfaceInvoke::getInterfaceId, interfaceInfoId);
		invokeLambdaQueryWrapper.eq(UserInterfaceInvoke::getUserId, userId);
		UserInterfaceInvoke userInterfaceInvoke = this.getOne(invokeLambdaQueryWrapper);
		// 不存在就创建一条记录
		boolean invokeResult;
		if (userInterfaceInvoke == null) {
			userInterfaceInvoke = new UserInterfaceInvoke();
			userInterfaceInvoke.setInterfaceId(interfaceInfoId);
			userInterfaceInvoke.setUserId(userId);
			userInterfaceInvoke.setTotalInvokes(1L);
			invokeResult = this.save(userInterfaceInvoke);
		} else {
			LambdaUpdateWrapper<UserInterfaceInvoke> invokeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
			invokeLambdaUpdateWrapper.eq(UserInterfaceInvoke::getInterfaceId, interfaceInfoId);
			invokeLambdaUpdateWrapper.eq(UserInterfaceInvoke::getUserId, userId);
			invokeLambdaUpdateWrapper.setSql("total_invokes = total_invokes + 1");
			invokeResult = this.update(invokeLambdaUpdateWrapper);
		}
		// 更新接口总调用次数
		boolean interfaceUpdateInvokeSave = interfaceInfoService.updateTotalInvokes(interfaceInfoId);
		// 更新用户钱包积分
		boolean reduceWalletBalanceResult = userService.reduceWalletBalance(userId, reduceScore);
		boolean updateResult = invokeResult && interfaceUpdateInvokeSave && reduceWalletBalanceResult;
		if (!updateResult) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "调用失败");
		}
		return true;
	}
}




