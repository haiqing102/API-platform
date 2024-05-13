package edu.cqupt.apigateway.filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.cqupt.apicommon.common.enums.InterfaceStatusEnum;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.enums.UserAccountStatusEnum;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.common.util.RedissonLockUtil;
import edu.cqupt.apicommon.model.dto.interfaceinfo.RequestParamsField;
import edu.cqupt.apicommon.model.entity.InterfaceInfo;
import edu.cqupt.apicommon.model.vo.UserVo;
import edu.cqupt.apicommon.service.dubbo.DubboInterfaceInfoService;
import edu.cqupt.apicommon.service.dubbo.DubboUserInterfaceInvokeService;
import edu.cqupt.apicommon.service.dubbo.DubboUserService;
import edu.cqupt.apisdk.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bouncycastle.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class GatewayGlobalFilter implements GlobalFilter, Ordered {
	/**
	 * 请求白名单
	 */
	private final static List<String> WHITE_HOST_LIST = Arrays.asList("127.0.0.1", "101.43.61.87");
	/**
	 * 五分钟过期时间
	 */
	private static final long FIVE_MINUTES = 5L * 60;
	@Resource
	private RedissonLockUtil redissonLockUtil;
	@DubboReference
	private DubboUserService dubboUserService;
	@DubboReference
	private DubboUserInterfaceInvokeService interfaceInvokeService;
	@DubboReference
	private DubboInterfaceInfoService interfaceInfoService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 日志
		ServerHttpRequest request = exchange.getRequest();
		log.info("【GlobalFilter】请求方法：{}，请求路径：{}，请求URL：{}",
				request.getMethod(), request.getPath(), request.getURI());

		// 请求的路径不是接口服务，直接放行
		if (!request.getPath().toString().contains("/api/interface"))
			return chain.filter(exchange);

		// 接口调用前进行安全检验
		return verifyParameters(exchange, chain);
	}

	/**
	 * 验证参数
	 *
	 * @param exchange 交换
	 * @param chain    链条
	 * @return {@link Mono}<{@link Void}>
	 */
	private Mono<Void> verifyParameters(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("【GlobalFilter】进行接口调用安全检验...");
		ServerHttpRequest request = exchange.getRequest();
		HttpHeaders headers = request.getHeaders();
		String body = headers.getFirst("body");
		String accessKey = headers.getFirst("accessKey");
		String timestamp = headers.getFirst("timestamp");
		String sign = headers.getFirst("sign");
		UserVo user = null;
		InterfaceInfo interfaceInfo = null;
		//try {
		// 请求头中参数必须完整
		if (StringUtils.isAnyBlank(body, sign, accessKey, timestamp)) {
			throw new BusinessException(ResponseCode.FORBIDDEN_ERROR);
		}
		// 防重发XHR
		long currentTime = System.currentTimeMillis() / 1000;
		assert timestamp != null;
		if (currentTime - Long.parseLong(timestamp) >= FIVE_MINUTES) {
			throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR, "会话已过期,请重试！");
		}


		user = dubboUserService.getInvokeUserByAccessKey(accessKey);
		if (user == null) {
			throw new BusinessException(ResponseCode.FORBIDDEN_ERROR, "账号不存在");
		}
		// 校验accessKey
		if (!user.getAccessKey().equals(accessKey)) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "请先获取请求密钥");
		}
		if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "该账号已封禁");
		}
		// 校验签名
		if (!SignUtil.getSign(body, user.getSecretKey()).equals(sign)) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "非法请求");
		}

		String method = Objects.requireNonNull(request.getMethod()).toString();
		String uri = request.getURI().toString().trim();

		if (StringUtils.isAnyBlank(uri, method)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		interfaceInfo = interfaceInfoService.getInterfaceInfo(uri, method);

		if (interfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "接口不存在");
		}
		if (user.getBalance() < interfaceInfo.getReduceScore()) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "余额不足，请先充值。");
		}
		if (interfaceInfo.getStatus() == InterfaceStatusEnum.AUDITING.getValue()) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "接口审核中");
		}
		if (interfaceInfo.getStatus() == InterfaceStatusEnum.OFFLINE.getValue()) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "接口未开启");
		}
		MultiValueMap<String, String> queryParams = request.getQueryParams();
		String requestParams = interfaceInfo.getRequestParams();
		List<RequestParamsField> list = new Gson().fromJson(requestParams, new TypeToken<List<RequestParamsField>>() {
		}.getType());
		if ("POST".equals(method)) {
			Object cacheBody = exchange.getAttribute(CacheBodyGatewayFilter.CACHE_REQUEST_BODY_OBJECT_KEY);
			String requestBody = getPostRequestBody((Flux<DataBuffer>) cacheBody);
			log.info("【GlobalFilter】POST请求参数：{}", requestBody);
			Map<String, Object> requestBodyMap = new Gson().fromJson(requestBody, new TypeToken<HashMap<String, Object>>() {
			}.getType());
			if (StringUtils.isNotBlank(requestParams)) {
				for (RequestParamsField requestParamsField : list) {
					if ("是".equals(requestParamsField.getRequired())) {
						if (StringUtils.isBlank((CharSequence) requestBodyMap.get(requestParamsField.getFieldName())) || !requestBodyMap.containsKey(requestParamsField.getFieldName())) {
							throw new BusinessException(ResponseCode.FORBIDDEN_ERROR, "请求参数错误，" + requestParamsField.getFieldName() + "不能为空");
						}
					}
				}
			}
		} else if ("GET".equals(method)) {
			log.info("【GlobalFilter】GET请求参数：{}", request.getQueryParams());
			// 校验请求参数
			if (StringUtils.isNotBlank(requestParams)) {
				for (RequestParamsField requestParamsField : list) {
					if ("是".equals(requestParamsField.getRequired())) {
						if (StringUtils.isBlank(queryParams.getFirst(requestParamsField.getFieldName())) || !queryParams.containsKey(requestParamsField.getFieldName())) {
							throw new BusinessException(ResponseCode.FORBIDDEN_ERROR, "请求参数错误，" + requestParamsField.getFieldName() + "不能为空");
						}
					}
				}
			}
		}
		//} catch (Exception e) {
		//	log.error("【GlobalFilter】Exception：{}", e.getMessage());
		//	return exchange.getResponse().setComplete();
		//}
		log.info("【GlobalFilter】安全检验通过。");
		return handleResponse(exchange, chain, user, interfaceInfo);
	}

	/**
	 * 获取post请求正文
	 *
	 * @param body 身体
	 * @return {@link String}
	 */
	private String getPostRequestBody(Flux<DataBuffer> body) {
		AtomicReference<String> getBody = new AtomicReference<>();
		body.subscribe(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);
			getBody.set(Strings.fromUTF8ByteArray(bytes));
		});
		return getBody.get();
	}

	/**
	 * 处理响应
	 *
	 * @param exchange 交换
	 * @param chain    链条
	 * @return {@link Mono}<{@link Void}>
	 */
	public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, UserVo user, InterfaceInfo interfaceInfo) {
		ServerHttpResponse originalResponse = exchange.getResponse();
		// 缓存数据的工厂
		DataBufferFactory bufferFactory = originalResponse.bufferFactory();
		// 拿到响应码
		HttpStatus statusCode = originalResponse.getStatusCode();
		//try {
		if (statusCode == HttpStatus.OK) {
			// 装饰，增强能力
			ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
				// 等调用完转发的接口后才会执行
				@Override
				public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
					if (body instanceof Flux) {
						Flux<? extends DataBuffer> fluxBody = Flux.from(body);
						// 往返回值里写数据
						return super.writeWith(
								fluxBody.map(dataBuffer -> {
									// 扣除积分
									redissonLockUtil.redissonDistributedLocks(("gateway_" + user.getUserAccount()).intern(), () -> {
										boolean invoke = interfaceInvokeService.invoke(interfaceInfo.getId(), user.getId(), interfaceInfo.getReduceScore());
										if (!invoke) {
											throw new BusinessException(ResponseCode.OPERATION_ERROR, "接口调用失败");
										}
									}, "接口调用失败");
									byte[] content = new byte[dataBuffer.readableByteCount()];
									dataBuffer.read(content);
									// 释放掉内存
									DataBufferUtils.release(dataBuffer);
									String data = new String(content, StandardCharsets.UTF_8);
									// 打印日志
									log.info("【GlobalFilter】接口调用结果：" + data);
									return bufferFactory.wrap(content);
								}));
					} else {
						// 8. 调用失败，返回一个规范的错误码
						log.error("【GlobalFilter】接口响应异常：{}", getStatusCode());
					}
					return super.writeWith(body);
				}
			};
			// 设置 response 对象为装饰过的
			return chain.filter(exchange.mutate().response(decoratedResponse).build());
		}
		//} catch (Exception e) {
		//	log.error("【GlobalFilter】Exception：{}", e.getMessage());
		//	return exchange.getResponse().setComplete();
		//}
		// 降级处理返回数据
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 100;
	}
}