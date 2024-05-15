package vin.suki.apibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vin.suki.apicommon.model.entity.RechargeActivity;

import java.util.List;

public interface RechargeActivityService extends IService<RechargeActivity> {
	/**
	 * 按订单号获取充值活动
	 *
	 * @param orderNo 订单号
	 * @return {@link RechargeActivity}
	 */
	List<RechargeActivity> getRechargeActivityByOrderNo(String orderNo);
}
