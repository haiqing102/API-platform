package vin.suki.apibackend.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfigKit;
import vin.suki.apibackend.common.config.AliPayConfig;
import vin.suki.apibackend.common.config.EmailConfig;
import vin.suki.apibackend.common.util.EmailUtil;
import vin.suki.apibackend.mapper.ProductOrderMapper;
import vin.suki.apibackend.service.PaymentInfoService;
import vin.suki.apibackend.service.ProductOrderService;
import vin.suki.apibackend.service.RechargeActivityService;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.enums.AlipayTradeStatusEnum;
import vin.suki.apicommon.common.enums.PaymentStatusEnum;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.common.util.RedissonLockUtil;
import vin.suki.apicommon.model.dto.pay.AliPayAsyncResponse;
import vin.suki.apicommon.model.entity.ProductInfo;
import vin.suki.apicommon.model.entity.ProductOrder;
import vin.suki.apicommon.model.entity.RechargeActivity;
import vin.suki.apicommon.model.entity.User;
import vin.suki.apicommon.model.vo.PaymentInfoVo;
import vin.suki.apicommon.model.vo.ProductOrderVo;
import vin.suki.apicommon.model.vo.UserVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vin.suki.apibackend.common.constant.PayConstant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import static vin.suki.apicommon.common.enums.PayTypeStatusEnum.ALIPAY;
import static vin.suki.apicommon.common.enums.PaymentStatusEnum.NOT_PAY;

@Service
@Slf4j
@Qualifier("ALIPAY")
public class AlipayOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {
	@Resource
	private EmailConfig emailConfig;
	@Resource
	private JavaMailSender mailSender;
	@Resource
	private AliPayConfig aliPayConfig;
	@Resource
	private UserService userService;
	@Resource
	private ProductInfoServiceImpl productInfoService;

	@Resource
	private PaymentInfoService paymentInfoService;
	@Resource
	private RedissonLockUtil redissonLockUtil;
	@Resource
	private RechargeActivityService rechargeActivityService;

	@Override
	public ProductOrderVo getProductOrder(Long productId, UserVo loginUser, String payType) {
		LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(ProductOrder::getProductId, productId);
		lambdaQueryWrapper.eq(ProductOrder::getStatus, NOT_PAY.getValue());
		lambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
		lambdaQueryWrapper.eq(ProductOrder::getUserId, loginUser.getId());
		ProductOrder oldOrder = this.getOne(lambdaQueryWrapper);
		if (oldOrder == null) {
			return null;
		}
		ProductOrderVo productOrderVo = new ProductOrderVo();
		BeanUtils.copyProperties(oldOrder, productOrderVo);
		productOrderVo.setProductInfo(JSONUtil.toBean(oldOrder.getProductInfo(), ProductInfo.class));
		productOrderVo.setTotal(oldOrder.getTotal().toString());
		return productOrderVo;
	}

	@Override
	public ProductOrderVo saveProductOrder(Long productId, UserVo loginUser) {
		ProductInfo productInfo = productInfoService.getById(productId);
		if (productInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "商品不存在");
		}
		// 5分钟有效期
		Date date = DateUtil.date(System.currentTimeMillis());
		Date expirationTime = DateUtil.offset(date, DateField.MINUTE, 5);
		String orderNo = PayConstant.ORDER_PREFIX + RandomUtil.randomNumbers(20);

		ProductOrder productOrder = new ProductOrder();
		productOrder.setUserId(loginUser.getId());
		productOrder.setOrderNo(orderNo);
		productOrder.setProductId(productInfo.getId());
		productOrder.setOrderName(productInfo.getName());
		productOrder.setTotal(productInfo.getTotal());
		productOrder.setStatus(NOT_PAY.getValue());
		productOrder.setPayType(ALIPAY.getValue());
		productOrder.setExpirationTime(expirationTime);
		productOrder.setProductInfo(JSONUtil.toJsonPrettyStr(productInfo));
		productOrder.setAddPoints(productInfo.getAddPoints());

		boolean saveResult = this.save(productOrder);

		AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		model.setOutTradeNo(orderNo);
		model.setSubject(productInfo.getName());
		model.setProductCode("FAST_INSTANT_TRADE_PAY");
		// 金额四舍五入
		BigDecimal scaledAmount = new BigDecimal(productInfo.getTotal()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		model.setTotalAmount(String.valueOf(scaledAmount));
		model.setBody(productInfo.getDescription());

		AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
		request.setBizModel(model);
		request.setNotifyUrl(aliPayConfig.getNotifyUrl());
		request.setReturnUrl(aliPayConfig.getReturnUrl());

		try {
			AlipayTradePagePayResponse alipayTradePagePayResponse = AliPayApi.pageExecute(request);
			String payUrl = alipayTradePagePayResponse.getBody();
			productOrder.setFormData(payUrl);
		} catch (AlipayApiException e) {
			throw new RuntimeException(e);
		}

		boolean updateResult = this.updateProductOrder(productOrder);
		if (!updateResult & !saveResult) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR);
		}
		// 构建vo
		ProductOrderVo productOrderVo = new ProductOrderVo();
		BeanUtils.copyProperties(productOrder, productOrderVo);
		productOrderVo.setProductInfo(productInfo);
		productOrderVo.setTotal(productInfo.getTotal().toString());
		return productOrderVo;
	}

	@Override
	public boolean updateProductOrder(ProductOrder productOrder) {
		String formData = productOrder.getFormData();
		Long id = productOrder.getId();
		ProductOrder updateCodeUrl = new ProductOrder();
		updateCodeUrl.setFormData(formData);
		updateCodeUrl.setId(id);
		return this.updateById(updateCodeUrl);
	}

	@Override
	public boolean updateOrderStatusByOrderNo(String outTradeNo, String orderStatus) {
		ProductOrder productOrder = new ProductOrder();
		productOrder.setStatus(orderStatus);
		LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
		return this.update(productOrder, lambdaQueryWrapper);
	}

	@Override
	public void closedOrderByOrderNo(String outTradeNo) throws AlipayApiException {
		AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
		alipayTradeCloseModel.setOutTradeNo(outTradeNo);
		AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
		request.setBizModel(alipayTradeCloseModel);
		AliPayApi.doExecute(request);
	}

	@Override
	public ProductOrder getProductOrderByOutTradeNo(String outTradeNo) {
		LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
		return this.getOne(lambdaQueryWrapper);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processingTimedOutOrders(ProductOrder productOrder) {
		String orderNo = productOrder.getOrderNo();
		try {
			// 查询订单
			AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
			alipayTradeQueryModel.setOutTradeNo(orderNo);
			AlipayTradeQueryResponse alipayTradeQueryResponse = AliPayApi.tradeQueryToResponse(alipayTradeQueryModel);

			// 本地创建了订单,但是用户没有扫码,支付宝端没有订单
			if (!alipayTradeQueryResponse.getCode().equals(PayConstant.RESPONSE_CODE_SUCCESS)) {
				// 更新本地订单状态
				this.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
				log.info("超时订单{},更新成功", orderNo);
				return;
			}
			String tradeStatus = AlipayTradeStatusEnum.findByName(alipayTradeQueryResponse.getTradeStatus()).getPaymentStatusEnum().getValue();
			// 订单没有支付就关闭订单,更新本地订单状态
			if (tradeStatus.equals(NOT_PAY.getValue()) || tradeStatus.equals(PaymentStatusEnum.CLOSED.getValue())) {
				closedOrderByOrderNo(orderNo);
				this.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
				log.info("超时订单{},关闭成功", orderNo);
				return;
			}
			if (tradeStatus.equals(PaymentStatusEnum.SUCCESS.getValue())) {
				// 订单已支付更新商户端的订单状态
				boolean updateOrderStatus = this.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.SUCCESS.getValue());
				// 补发积分到用户钱包
				boolean addWalletBalance = userService.addWalletBalance(productOrder.getUserId(), productOrder.getAddPoints());
				// 保存支付记录
				PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
				paymentInfoVo.setAppid(aliPayConfig.getAppId());
				paymentInfoVo.setOutTradeNo(alipayTradeQueryResponse.getOutTradeNo());
				paymentInfoVo.setTransactionId(alipayTradeQueryResponse.getTradeNo());
				paymentInfoVo.setTradeType("电脑网站支付");
				paymentInfoVo.setTradeState(alipayTradeQueryResponse.getTradeStatus());
				paymentInfoVo.setTradeStateDesc("支付成功");
				paymentInfoVo.setSuccessTime(String.valueOf(alipayTradeQueryResponse.getSendPayDate()));
				WxPayOrderQueryV3Result.Payer payer = new WxPayOrderQueryV3Result.Payer();
				payer.setOpenid(alipayTradeQueryResponse.getBuyerOpenId());
				paymentInfoVo.setPayer(payer);
				WxPayOrderQueryV3Result.Amount amount = new WxPayOrderQueryV3Result.Amount();
				amount.setTotal(new BigDecimal(alipayTradeQueryResponse.getTotalAmount()).multiply(new BigDecimal("100")).intValue());
				amount.setPayerTotal(new BigDecimal(alipayTradeQueryResponse.getReceiptAmount()).multiply(new BigDecimal("100")).intValue());
				amount.setCurrency(alipayTradeQueryResponse.getPayCurrency());
				amount.setPayerCurrency(alipayTradeQueryResponse.getPayCurrency());
				paymentInfoVo.setAmount(amount);
				boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
				if (!updateOrderStatus & !addWalletBalance & !paymentResult) {
					throw new BusinessException(ResponseCode.OPERATION_ERROR);
				}
				// 更新活动表
				saveRechargeActivity(productOrder);
				sendSuccessEmail(productOrder, alipayTradeQueryResponse.getTotalAmount());
				log.info("超时订单{},更新成功", orderNo);
			}
		} catch (AlipayApiException e) {
			log.error("订单{} 处理失败", orderNo);
			throw new BusinessException(ResponseCode.OPERATION_ERROR, e.getMessage());
		}

	}

	private void sendSuccessEmail(ProductOrder productOrder, String orderTotal) {
		// 发送邮件
		User user = userService.getById(productOrder.getUserId());
		if (StringUtils.isNotBlank(user.getEmail())) {
			try {
				ProductOrder productOrderByOutTradeNo = this.getProductOrderByOutTradeNo(productOrder.getOrderNo());
				new EmailUtil().sendPaySuccessEmail(user.getEmail(), mailSender, emailConfig, productOrderByOutTradeNo.getOrderName(),
						String.valueOf(orderTotal));
				log.info("发送邮件：{}，成功", user.getEmail());
			} catch (Exception e) {
				log.error("发送邮件：{}，失败：{}", user.getEmail(), e.getMessage());
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String doPaymentNotify(String notifyData, HttpServletRequest request) {
		Map<String, String> params = AliPayApi.toMap(request);
		AliPayAsyncResponse aliPayAsyncResponse = JSONUtil.toBean(JSONUtil.toJsonStr(params), AliPayAsyncResponse.class);
		String lockName = "notify:AlipayOrder:lock:" + aliPayAsyncResponse.getOutTradeNo();
		return redissonLockUtil.redissonDistributedLocks(lockName, "【支付宝异步回调异常】:", () -> {
			String result;
			try {
				result = checkAlipayOrder(aliPayAsyncResponse, params);
			} catch (AlipayApiException e) {
				throw new BusinessException(ResponseCode.OPERATION_ERROR, e.getMessage());
			}
			if (!"success".equals(result)) {
				return result;
			}
			String doAliPayOrderBusinessResult = this.doAliPayOrderBusiness(aliPayAsyncResponse);
			if (StringUtils.isBlank(doAliPayOrderBusinessResult)) {
				throw new BusinessException(ResponseCode.OPERATION_ERROR);
			}
			return doAliPayOrderBusinessResult;
		});
	}

	private String checkAlipayOrder(AliPayAsyncResponse response, Map<String, String> params) throws AlipayApiException {
		String result = "failure";
		boolean verifyResult = AlipaySignature.rsaCheckV1(params, AliPayApiConfigKit.getAliPayApiConfig().getAliPayPublicKey(),
				AliPayApiConfigKit.getAliPayApiConfig().getCharset(),
				AliPayApiConfigKit.getAliPayApiConfig().getSignType());
		if (!verifyResult) {
			return result;
		}
		// 1.验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
		ProductOrder productOrder = this.getProductOrderByOutTradeNo(response.getOutTradeNo());
		if (productOrder == null) {
			log.error("订单不存在");
			return result;
		}
		// 2.判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
		int totalAmount = new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal("100")).intValue();
		if (totalAmount != productOrder.getTotal()) {
			log.error("订单金额不一致");
			return result;
		}
		// 3.校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
		String sellerId = aliPayConfig.getSellerId();
		if (!response.getSellerId().equals(sellerId)) {
			log.error("卖家账号校验失败");
			return result;
		}
		// 4.验证 app_id 是否为该商家本身。
		String appId = aliPayConfig.getAppId();
		if (!response.getAppId().equals(appId)) {
			log.error("校验失败");
			return result;
		}
		// 状态 TRADE_SUCCESS 的通知触发条件是商家开通的产品支持退款功能的前提下，买家付款成功。
		String tradeStatus = response.getTradeStatus();
		if (!tradeStatus.equals(PayConstant.TRADE_SUCCESS)) {
			log.error("交易失败");
			return result;
		}
		return "success";
	}

	@SneakyThrows
	protected String doAliPayOrderBusiness(AliPayAsyncResponse response) {
		String outTradeNo = response.getOutTradeNo();
		ProductOrder productOrder = this.getProductOrderByOutTradeNo(outTradeNo);
		// 处理重复通知
		if (PaymentStatusEnum.SUCCESS.getValue().equals(productOrder.getStatus())) {
			return "success";
		}
		// 业务代码
		// 更新订单状态
		boolean updateOrderStatus = this.updateOrderStatusByOrderNo(outTradeNo, PaymentStatusEnum.SUCCESS.getValue());
		// 更新用户积分
		boolean addWalletBalance = userService.addWalletBalance(productOrder.getUserId(), productOrder.getAddPoints());
		// 保存支付记录
		PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
		paymentInfoVo.setAppid(response.getAppId());
		paymentInfoVo.setOutTradeNo(response.getOutTradeNo());
		paymentInfoVo.setTransactionId(response.getTradeNo());
		paymentInfoVo.setTradeType("电脑网站支付");
		paymentInfoVo.setTradeState(response.getTradeStatus());
		paymentInfoVo.setTradeStateDesc("支付成功");
		paymentInfoVo.setSuccessTime(response.getNotifyTime());
		WxPayOrderQueryV3Result.Payer payer = new WxPayOrderQueryV3Result.Payer();
		payer.setOpenid(response.getBuyerId());
		paymentInfoVo.setPayer(payer);
		WxPayOrderQueryV3Result.Amount amount = new WxPayOrderQueryV3Result.Amount();
		amount.setTotal(new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal("100")).intValue());
		amount.setPayerTotal(new BigDecimal(response.getReceiptAmount()).multiply(new BigDecimal("100")).intValue());
		amount.setCurrency("CNY");
		amount.setPayerCurrency("CNY");
		paymentInfoVo.setAmount(amount);
		boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
		// 更新活动表
		boolean rechargeActivity = saveRechargeActivity(productOrder);
		if (paymentResult && updateOrderStatus && addWalletBalance && rechargeActivity) {
			log.info("【支付回调通知处理成功】");
			// 发送邮件
			sendSuccessEmail(productOrder, response.getTotalAmount());
			return "success";
		}
		throw new BusinessException(ResponseCode.OPERATION_ERROR);
	}


	/**
	 * 保存充值活动
	 *
	 * @param productOrder 产品订单
	 * @return boolean
	 */
	private boolean saveRechargeActivity(ProductOrder productOrder) {
		RechargeActivity rechargeActivity = new RechargeActivity();
		rechargeActivity.setUserId(productOrder.getUserId());
		rechargeActivity.setProductId(productOrder.getProductId());
		rechargeActivity.setOrderNo(productOrder.getOrderNo());
		boolean save = rechargeActivityService.save(rechargeActivity);
		if (!save) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "保存失败");
		}
		return true;
	}
}




