package vin.suki.apicommon.model.vo;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentInfoVo {

	private String appid;

	private String mchid;

	private String outTradeNo;

	private String transactionId;

	private String tradeType;

	private String tradeState;

	private String tradeStateDesc;

	private String bankType;

	private String attach;

	private String successTime;

	private WxPayOrderQueryV3Result.Payer payer;

	@SerializedName(value = "amount")
	private WxPayOrderQueryV3Result.Amount amount;

	@SerializedName(value = "scene_info")
	private WxPayOrderQueryV3Result.SceneInfo sceneInfo;

	@SerializedName(value = "promotion_detail")
	private List<WxPayOrderQueryV3Result.PromotionDetail> promotionDetails;

}
