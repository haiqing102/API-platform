package vin.suki.apibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vin.suki.apicommon.model.entity.PaymentInfo;
import vin.suki.apicommon.model.vo.PaymentInfoVo;

public interface PaymentInfoService extends IService<PaymentInfo> {
	/**
	 * 创建付款信息
	 *
	 * @param paymentInfoVo 付款信息vo
	 * @return boolean
	 */
	boolean createPaymentInfo(PaymentInfoVo paymentInfoVo);
}
