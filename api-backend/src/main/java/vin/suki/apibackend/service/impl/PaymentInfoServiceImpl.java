package vin.suki.apibackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import vin.suki.apibackend.mapper.PaymentInfoMapper;
import vin.suki.apibackend.service.PaymentInfoService;
import vin.suki.apicommon.model.entity.PaymentInfo;
import vin.suki.apicommon.model.vo.PaymentInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Override
    public boolean createPaymentInfo(PaymentInfoVo paymentInfoVo) {
        String transactionId = paymentInfoVo.getTransactionId();
        String tradeType = paymentInfoVo.getTradeType();
        String tradeState = paymentInfoVo.getTradeState();
        String tradeStateDesc = paymentInfoVo.getTradeStateDesc();
        String successTime = paymentInfoVo.getSuccessTime();
        WxPayOrderQueryV3Result.Payer payer = paymentInfoVo.getPayer();
        WxPayOrderQueryV3Result.Amount amount = paymentInfoVo.getAmount();

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(paymentInfoVo.getOutTradeNo());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType(tradeType);
        paymentInfo.setTradeState(tradeState);
        if (StringUtils.isNotBlank(successTime)) {
            paymentInfo.setSuccessTime(successTime);
        }
        paymentInfo.setOpenid(payer.getOpenid());
        paymentInfo.setPayerTotal(amount.getPayerTotal());
        paymentInfo.setCurrency(amount.getCurrency());
        paymentInfo.setPayerCurrency(amount.getPayerCurrency());
        paymentInfo.setTotal(amount.getTotal());
        paymentInfo.setTradeStateDesc(tradeStateDesc);
        paymentInfo.setContent(JSONUtil.toJsonStr(paymentInfoVo));
        return this.save(paymentInfo);
    }
}




