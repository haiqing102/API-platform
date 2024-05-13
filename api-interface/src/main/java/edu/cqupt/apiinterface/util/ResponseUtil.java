package edu.cqupt.apiinterface.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apisdk.model.response.ResultResponse;

import java.util.Map;

import static edu.cqupt.apiinterface.util.RequestUtil.get;

public class ResponseUtil {

	public static Map<String, Object> responseToMap(String response) {
		return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
		}.getType());
	}

	public static <T> ResultResponse baseResponse(String baseUrl, T params) {
		String response = null;
		try {
			if (params == null)
				response = get(baseUrl);
			else
				response = get(baseUrl, params);
			Map<String, Object> fromResponse = responseToMap(response);
			boolean success = (boolean) fromResponse.get("success");
			ResultResponse baseResponse = new ResultResponse();
			if (!success) {
				baseResponse.setData(fromResponse);
				return baseResponse;
			}
			fromResponse.remove("success");
			baseResponse.setData(fromResponse);
			return baseResponse;
		} catch (BusinessException e) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "构建url异常");
		}
	}

}
