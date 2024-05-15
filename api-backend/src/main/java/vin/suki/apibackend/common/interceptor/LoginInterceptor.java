package vin.suki.apibackend.common.interceptor;

import cn.hutool.json.JSONUtil;
import vin.suki.apicommon.common.util.ResponseUtil;
import vin.suki.apicommon.model.vo.UserVo;
import org.springframework.web.servlet.HandlerInterceptor;
import vin.suki.apibackend.common.constant.UserConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static vin.suki.apicommon.common.enums.ResponseCode.NOT_LOGIN_ERROR;

public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserVo user = (UserVo) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		if (user == null) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(JSONUtil.toJsonStr(ResponseUtil.error(NOT_LOGIN_ERROR)));
			return false;
		}
		return true;
	}

}
