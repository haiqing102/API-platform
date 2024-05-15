package vin.suki.apibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import vin.suki.apibackend.mapper.InterfaceInfoMapper;
import vin.suki.apibackend.service.InterfaceInfoService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {
	@Override
	public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
		if (interfaceInfo == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		String name = interfaceInfo.getName();
		String url = interfaceInfo.getUrl();
		Long userId = interfaceInfo.getUserId();
		String method = interfaceInfo.getMethod();

		String requestParams = interfaceInfo.getRequestParams();
		String description = interfaceInfo.getDescription();
		String requestExample = interfaceInfo.getRequestExample();
		String requestHeader = interfaceInfo.getRequestHeader();
		String responseHeader = interfaceInfo.getResponseHeader();
		Integer status = interfaceInfo.getStatus();
		Integer reduceScore = interfaceInfo.getReduceScore();
		// 创建时，所有参数必须非空
		if (add) {
			if (StringUtils.isAnyBlank(name, url, method)) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR);
			}
		}
		if (StringUtils.isNotBlank(method)) {
			interfaceInfo.setMethod(method.trim().toUpperCase());
		}
		if (StringUtils.isNotBlank(url)) {
			interfaceInfo.setUrl(url.trim());
		}
		if (ObjectUtils.isNotEmpty(reduceScore) && reduceScore < 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "扣除积分个数不能为负数");
		}
		if (StringUtils.isNotBlank(name) && name.length() > 60) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "接口名称过长");
		}

		if (StringUtils.isNotBlank(description) && description.length() > 100) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "接口描述过长");
		}
	}


	@Override
	public boolean updateTotalInvokes(long interfaceId) {
		LambdaUpdateWrapper<InterfaceInfo> invokeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		invokeLambdaUpdateWrapper.eq(InterfaceInfo::getId, interfaceId);
		invokeLambdaUpdateWrapper.setSql("total_invokes = total_invokes + 1");
		return this.update(invokeLambdaUpdateWrapper);
	}
}




