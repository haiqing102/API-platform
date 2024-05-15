package vin.suki.apibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import vin.suki.apibackend.mapper.ProductInfoMapper;
import vin.suki.apibackend.service.ProductInfoService;
import vin.suki.apicommon.common.enums.ResponseCode;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.entity.ProductInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo>
		implements ProductInfoService {
	@Override
	public void validProductInfo(ProductInfo productInfo, boolean add) {
		if (productInfo == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		String name = productInfo.getName();
		String description = productInfo.getDescription();
		Integer total = productInfo.getTotal();
		Date expirationTime = productInfo.getExpirationTime();
		String productType = productInfo.getProductType();
		Integer addPoints = productInfo.getAddPoints();
		// 创建时，所有参数必须非空
		if (add) {
			if (StringUtils.isAnyBlank(name)) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR);
			}
		}
		if (addPoints < 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "增加积分不能为负数");
		}
		if (total < 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "售卖金额不能为负数");
		}
	}
}




