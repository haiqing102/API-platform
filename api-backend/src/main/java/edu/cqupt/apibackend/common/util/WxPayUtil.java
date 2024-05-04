package edu.cqupt.apibackend.common.util;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;

import javax.servlet.http.HttpServletRequest;

public class WxPayUtil {
	/**
	 * 获取回调请求头：签名相关
	 *
	 * @param request HttpServletRequest
	 * @return SignatureHeader
	 */
	public static SignatureHeader getRequestHeader(HttpServletRequest request) {
		// 获取通知签名
		String signature = request.getHeader("Wechatpay-Signature");
		String nonce = request.getHeader("Wechatpay-Nonce");
		String serial = request.getHeader("Wechatpay-Serial");
		String timestamp = request.getHeader("Wechatpay-Timestamp");
		SignatureHeader signatureHeader = new SignatureHeader();
		signatureHeader.setSignature(signature);
		signatureHeader.setNonce(nonce);
		signatureHeader.setSerial(serial);
		signatureHeader.setTimeStamp(timestamp);
		return signatureHeader;
	}
}
