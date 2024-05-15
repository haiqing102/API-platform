package vin.suki.apibackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import vin.suki.apisdk.client.ApiClient;
import vin.suki.apisdk.model.request.CurrencyRequest;
import vin.suki.apisdk.model.response.ResultResponse;
import vin.suki.apisdk.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vin.suki.apibackend.common.annotation.AuthCheck;
import vin.suki.apibackend.common.constant.UserConstant;
import vin.suki.apibackend.common.request.DeleteRequest;
import vin.suki.apibackend.common.request.IdRequest;
import vin.suki.apibackend.service.InterfaceInfoService;
import vin.suki.apibackend.service.UserService;
import vin.suki.apicommon.common.constant.CommonConstant;
import vin.suki.apicommon.common.enums.InterfaceStatusEnum;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.common.response.BaseResponse;
import vin.suki.apicommon.common.util.ResponseUtil;
import vin.suki.apicommon.model.dto.interfaceinfo.*;
import vin.suki.apicommon.model.entity.InterfaceInfo;
import vin.suki.apicommon.model.entity.User;
import vin.suki.apicommon.model.vo.UserVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static vin.suki.apicommon.common.enums.ResponseCode.OPERATION_ERROR;

@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {
	@Resource
	private InterfaceInfoService interfaceInfoService;
	@Resource
	private UserService userService;
	@Resource
	private ApiService apiService;

	private final Gson gson = new Gson();

	/**
	 * 添加接口信息
	 * 创建
	 *
	 * @param interfaceInfoAddRequest 接口信息添加请求
	 * @param request                 请求
	 * @return {@link BaseResponse}<{@link Long}>
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
		if (interfaceInfoAddRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		InterfaceInfo interfaceInfo = new InterfaceInfo();
		if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getRequestParams())) {
			List<RequestParamsField> requestParamsFields = interfaceInfoAddRequest.getRequestParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
			String requestParams = JSONUtil.toJsonStr(requestParamsFields);
			interfaceInfo.setRequestParams(requestParams);
		}
		if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getResponseParams())) {
			List<ResponseParamsField> responseParamsFields = interfaceInfoAddRequest.getResponseParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
			String responseParams = JSONUtil.toJsonStr(responseParamsFields);
			interfaceInfo.setResponseParams(responseParams);
		}
		BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
		// 校验
		interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
		UserVo loginUser = userService.getLoginUser(request);
		interfaceInfo.setUserId(loginUser.getId());
		boolean result = interfaceInfoService.save(interfaceInfo);
		if (!result) {
			throw new BusinessException(OPERATION_ERROR);
		}
		long newInterfaceInfoId = interfaceInfo.getId();
		return ResponseUtil.success(newInterfaceInfoId);
	}

	/**
	 * 删除接口信息
	 *
	 * @param deleteRequest 删除请求
	 * @param request       请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		UserVo user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
		if (oldInterfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		// 仅本人或管理员可删除
		if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
		}
		boolean b = interfaceInfoService.removeById(id);
		return ResponseUtil.success(b);
	}

	/**
	 * 更新接口头像url
	 *
	 * @param request                          请求
	 * @param interfaceInfoUpdateAvatarRequest 界面信息更新头像请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/updateInterfaceInfoAvatar")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateInterfaceInfoAvatarUrl(@RequestBody InterfaceInfoUpdateAvatarRequest interfaceInfoUpdateAvatarRequest,
															  HttpServletRequest request) {
		if (ObjectUtils.anyNull(interfaceInfoUpdateAvatarRequest, interfaceInfoUpdateAvatarRequest.getId()) || interfaceInfoUpdateAvatarRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		InterfaceInfo interfaceInfo = new InterfaceInfo();
		BeanUtils.copyProperties(interfaceInfoUpdateAvatarRequest, interfaceInfo);
		return ResponseUtil.success(interfaceInfoService.updateById(interfaceInfo));
	}

	/**
	 * 更新接口信息
	 *
	 * @param interfaceInfoUpdateRequest 接口信息更新请求
	 * @param request                    请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
													 HttpServletRequest request) {
		if (ObjectUtils.anyNull(interfaceInfoUpdateRequest, interfaceInfoUpdateRequest.getId()) || interfaceInfoUpdateRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		InterfaceInfo interfaceInfo = new InterfaceInfo();
		if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getRequestParams())) {
			List<RequestParamsField> requestParamsFields = interfaceInfoUpdateRequest.getRequestParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
			String requestParams = JSONUtil.toJsonStr(requestParamsFields);
			interfaceInfo.setRequestParams(requestParams);
		}
		if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getResponseParams())) {
			List<ResponseParamsField> responseParamsFields = interfaceInfoUpdateRequest.getResponseParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
			String responseParams = JSONUtil.toJsonStr(responseParamsFields);
			interfaceInfo.setResponseParams(responseParams);
		}
		BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
		// 参数校验
		interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
		UserVo user = userService.getLoginUser(request);
		long id = interfaceInfoUpdateRequest.getId();
		// 判断是否存在
		InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
		if (oldInterfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		// 仅本人或管理员可修改
		if (!userService.isAdmin(request) && !oldInterfaceInfo.getUserId().equals(user.getId())) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
		}
		boolean result = interfaceInfoService.updateById(interfaceInfo);
		return ResponseUtil.success(result);
	}

	/**
	 * 通过id获取接口信息
	 *
	 * @param id id
	 * @return {@link BaseResponse}<{@link InterfaceInfo}>
	 */
	@GetMapping("/get")
	public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
		if (id <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
		return ResponseUtil.success(interfaceInfo);
	}

	/**
	 * 获取列表（仅管理员可使用）
	 *
	 * @param interfaceInfoQueryRequest 接口信息查询请求
	 * @return {@link BaseResponse}<{@link List}<{@link InterfaceInfo}>>
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@GetMapping("/list")
	public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
		if (interfaceInfoQueryRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
		BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);

		QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
		List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
		return ResponseUtil.success(interfaceInfoList);
	}

	/**
	 * 分页获取列表
	 *
	 * @param interfaceInfoQueryRequest 接口信息查询请求
	 * @param request                   请求
	 * @return {@link BaseResponse}<{@link Page}<{@link InterfaceInfo}>>
	 */
	@GetMapping("/list/page")
	public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
		if (interfaceInfoQueryRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}

		InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
		BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
		long size = interfaceInfoQueryRequest.getPageSize();
		String sortField = interfaceInfoQueryRequest.getSortField();
		String sortOrder = interfaceInfoQueryRequest.getSortOrder();
		String url = interfaceInfoQueryRequest.getUrl();

		String name = interfaceInfoQueryRequest.getName();
		long current = interfaceInfoQueryRequest.getCurrent();
		String method = interfaceInfoQueryRequest.getMethod();
		String description = interfaceInfoQueryRequest.getDescription();
		Integer status = interfaceInfoQueryRequest.getStatus();
		Integer reduceScore = interfaceInfoQueryRequest.getReduceScore();
		String returnFormat = interfaceInfoQueryRequest.getReturnFormat();
		// 限制爬虫
		if (size > 50) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
				.like(StringUtils.isNotBlank(description), "description", description)
				.like(StringUtils.isNotBlank(url), "url", url)
				.like(StringUtils.isNotBlank(returnFormat), "returnFormat", returnFormat)
				.eq(StringUtils.isNotBlank(method), "method", method)
				.eq(ObjectUtils.isNotEmpty(status), "status", status)
				.eq(ObjectUtils.isNotEmpty(reduceScore), "reduceScore", reduceScore);
		queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
		Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
		User user = userService.isTourist(request);
		// 不是管理员只能查看已经上线的
		if (user == null || !user.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
			List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords().stream()
					.filter(interfaceInfo -> interfaceInfo.getStatus().equals(InterfaceStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
			interfaceInfoPage.setRecords(interfaceInfoList);
		}
		return ResponseUtil.success(interfaceInfoPage);
	}

	/**
	 * 按搜索文本页查询数据
	 *
	 * @param interfaceInfoQueryRequest 接口信息查询请求
	 * @param request                   请求
	 * @return {@link BaseResponse}<{@link Page}<{@link InterfaceInfo}>>
	 */
	@GetMapping("/get/searchText")
	public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoBySearchTextPage(InterfaceInfoSearchTextRequest interfaceInfoQueryRequest, HttpServletRequest request) {
		if (interfaceInfoQueryRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}

		String searchText = interfaceInfoQueryRequest.getSearchText();
		long size = interfaceInfoQueryRequest.getPageSize();
		long current = interfaceInfoQueryRequest.getCurrent();
		String sortField = interfaceInfoQueryRequest.getSortField();
		String sortOrder = interfaceInfoQueryRequest.getSortOrder();

		QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(searchText), "name", searchText)
				.or()
				.like(StringUtils.isNotBlank(searchText), "description", searchText);
		queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
		Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
		// 不是管理员只能查看已经上线的
		if (!userService.isAdmin(request)) {
			List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords().stream()
					.filter(interfaceInfo -> interfaceInfo.getStatus().equals(InterfaceStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
			interfaceInfoPage.setRecords(interfaceInfoList);
		}
		return ResponseUtil.success(interfaceInfoPage);
	}

	/**
	 * 发布
	 *
	 * @param idRequest id请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@PostMapping("/online")
	public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		Long id = idRequest.getId();
		InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
		if (interfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		interfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
		return ResponseUtil.success(interfaceInfoService.updateById(interfaceInfo));
	}

	/**
	 * 下线
	 *
	 * @param idRequest id请求
	 * @param request   请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/offline")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		Long id = idRequest.getId();
		InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
		if (interfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		interfaceInfo.setStatus(InterfaceStatusEnum.OFFLINE.getValue());
		return ResponseUtil.success(interfaceInfoService.updateById(interfaceInfo));
	}

	// endregion

	/**
	 * 调用接口
	 *
	 * @param invokeRequest id请求
	 * @param request       请求
	 * @return {@link BaseResponse}<{@link Object}>
	 */
	@PostMapping("/invoke")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Object> invokeInterface(@RequestBody InvokeRequest invokeRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(invokeRequest, invokeRequest.getId()) || invokeRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		Long id = invokeRequest.getId();
		InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
		if (interfaceInfo == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		if (interfaceInfo.getStatus() != InterfaceStatusEnum.ONLINE.getValue()) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "接口未开启");
		}
		UserVo loginUser = userService.getLoginUser(request);
		if (loginUser.getBalance() < interfaceInfo.getReduceScore()) {
			throw new BusinessException(50001, "余额不足，请先充值。");
		}
		// 构建请求参数
		List<InvokeRequest.Field> fieldList = invokeRequest.getRequestParams();
		String requestParams = "{}";
		if (fieldList != null && fieldList.size() > 0) {
			JsonObject jsonObject = new JsonObject();
			for (InvokeRequest.Field field : fieldList) {
				if (StringUtils.isNotBlank(field.getValue()))
					jsonObject.addProperty(field.getFieldName(), field.getValue());
			}
			requestParams = gson.toJson(jsonObject);
		}
		Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
		}.getType());
		String accessKey = loginUser.getAccessKey();
		String secretKey = loginUser.getSecretKey();
		try {
			ApiClient apiClient = new ApiClient(accessKey, secretKey);
			CurrencyRequest currencyRequest = new CurrencyRequest();
			currencyRequest.setMethod(interfaceInfo.getMethod());
			currencyRequest.setPath(interfaceInfo.getUrl());
			currencyRequest.setRequestParams(params);

			ResultResponse response = apiService.request(apiClient, currencyRequest);
			Map<String, Object> data = response.getData();

			// 处理网关抛出的异常
			if (data.get("code") != null && (double) data.get("code") == 403)
				return ResponseUtil.error(OPERATION_ERROR, (String) data.get("message"));

			return ResponseUtil.success(data);
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.SYSTEM_ERROR, e.getMessage());
		}
	}
}
