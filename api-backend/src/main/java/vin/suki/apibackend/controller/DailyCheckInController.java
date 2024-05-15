package vin.suki.apibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import vin.suki.apibackend.service.DailyCheckInService;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.common.response.BaseResponse;
import vin.suki.apicommon.common.util.RedissonLockUtil;
import vin.suki.apicommon.common.util.ResponseUtil;
import vin.suki.apicommon.model.entity.DailyCheckIn;
import vin.suki.apicommon.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dailyCheckIn")
@Slf4j
public class DailyCheckInController {

	@Resource
	private DailyCheckInService dailyCheckInService;

	@Resource
	private UserService userService;
	@Resource
	private RedissonLockUtil redissonLockUtil;

	// region 增删改查

	/**
	 * 签到
	 *
	 * @param request 请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/doCheckIn")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> doDailyCheckIn(HttpServletRequest request) {
		UserVo loginUser = userService.getLoginUser(request);
		String redissonLock = ("doDailyCheckIn_" + loginUser.getUserAccount()).intern();
		return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
			LambdaQueryWrapper<DailyCheckIn> dailyCheckInLambdaQueryWrapper = new LambdaQueryWrapper<>();
			dailyCheckInLambdaQueryWrapper.eq(DailyCheckIn::getUserId, loginUser.getId());
			DailyCheckIn dailyCheckIn = dailyCheckInService.getOne(dailyCheckInLambdaQueryWrapper);
			if (ObjectUtils.isNotEmpty(dailyCheckIn)) {
				throw new BusinessException(ResponseCode.OPERATION_ERROR, "签到失败,今日已签到");
			}
			dailyCheckIn = new DailyCheckIn();
			dailyCheckIn.setUserId(loginUser.getId());
			dailyCheckIn.setAddPoints(10);
			boolean dailyCheckInResult = dailyCheckInService.save(dailyCheckIn);
			boolean addWalletBalance = userService.addWalletBalance(loginUser.getId(), dailyCheckIn.getAddPoints());
			boolean result = dailyCheckInResult & addWalletBalance;
			if (!result) {
				throw new BusinessException(ResponseCode.OPERATION_ERROR);
			}
			return ResponseUtil.success(true);
		}, "签到失败");
	}
	// endregion
}
