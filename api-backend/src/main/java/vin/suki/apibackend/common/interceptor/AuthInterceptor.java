package vin.suki.apibackend.common.interceptor;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import vin.suki.apibackend.common.annotation.AuthCheck;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 权限校验 AOP
 */
@Aspect
@Component
public class AuthInterceptor {

	@Resource
	private UserService userService;

	/**
	 * 执行拦截
	 *
	 * @param joinPoint 连接点
	 * @param authCheck 身份验证检查
	 * @return {@link Object}
	 * @throws Throwable 可丢弃
	 */
	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		String mustRole = authCheck.mustRole();
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		// 当前登录用户
		UserVo user = userService.getLoginUser(request);
		// 拥有任意权限即通过
		if (CollectionUtils.isNotEmpty(anyRole)) {
			String userRole = user.getUserRole();
			if (!anyRole.contains(userRole)) {
				throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
			}
		}
		// 必须有所有权限才通过
		if (StringUtils.isNotBlank(mustRole)) {
			String userRole = user.getUserRole();
			if (!mustRole.equals(userRole)) {
				throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
			}
		}
		// 通过权限校验，放行
		return joinPoint.proceed();
	}
}

