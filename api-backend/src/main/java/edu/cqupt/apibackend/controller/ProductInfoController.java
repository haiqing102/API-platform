package edu.cqupt.apibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.apibackend.common.annotation.AuthCheck;
import edu.cqupt.apibackend.common.request.DeleteRequest;
import edu.cqupt.apibackend.common.request.IdRequest;
import edu.cqupt.apibackend.service.ProductInfoService;
import edu.cqupt.apibackend.service.UserService;
import edu.cqupt.apicommon.common.constant.CommonConstant;
import edu.cqupt.apicommon.common.enums.ProductInfoStatusEnum;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.common.response.BaseResponse;
import edu.cqupt.apicommon.common.util.ResponseUtil;
import edu.cqupt.apicommon.model.dto.productinfo.ProductInfoAddRequest;
import edu.cqupt.apicommon.model.dto.productinfo.ProductInfoQueryRequest;
import edu.cqupt.apicommon.model.dto.productinfo.ProductInfoSearchTextRequest;
import edu.cqupt.apicommon.model.dto.productinfo.ProductInfoUpdateRequest;
import edu.cqupt.apicommon.model.entity.ProductInfo;
import edu.cqupt.apicommon.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static edu.cqupt.apibackend.common.constant.UserConstant.ADMIN_ROLE;

@RestController
@RequestMapping("/productInfo")
@Slf4j
public class ProductInfoController {

    @Resource
    private ProductInfoService productInfoService;

    @Resource
    private UserService userService;


    // region 增删改查

    /**
     * 添加商品信息
     * 创建
     *
     * @param productInfoAddRequest 商品信息添加请求
     * @param request               请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addProductInfo(@RequestBody ProductInfoAddRequest productInfoAddRequest, HttpServletRequest request) {
        if (productInfoAddRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoAddRequest, productInfo);
        // 校验
        productInfoService.validProductInfo(productInfo, true);
        UserVo loginUser = userService.getLoginUser(request);
        productInfo.setUserId(loginUser.getId());
        boolean result = productInfoService.save(productInfo);
        if (!result) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR);
        }
        long newProductInfoId = productInfo.getId();
        return ResponseUtil.success(newProductInfoId);
    }

    /**
     * 删除商品信息
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProductInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        UserVo user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ProductInfo oldProductInfo = productInfoService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldProductInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
        }
        boolean b = productInfoService.removeById(id);
        return ResponseUtil.success(b);
    }

    /**
     * 更新商品信息
     * 更新
     *
     * @param productInfoUpdateRequest 商品信息更新请求
     * @param request                  请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateProductInfo(@RequestBody ProductInfoUpdateRequest productInfoUpdateRequest,
                                                   HttpServletRequest request) {
        if (ObjectUtils.anyNull(productInfoUpdateRequest, productInfoUpdateRequest.getId()) || productInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoUpdateRequest, productInfo);
        // 参数校验
        productInfoService.validProductInfo(productInfo, false);
        UserVo user = userService.getLoginUser(request);
        long id = productInfoUpdateRequest.getId();
        // 判断是否存在
        ProductInfo oldProductInfo = productInfoService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!userService.isAdmin(request) && !oldProductInfo.getUserId().equals(user.getId())) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
        }
        boolean result = productInfoService.updateById(productInfo);
        return ResponseUtil.success(result);
    }

    /**
     * 通过id获取商品信息
     *
     * @param id id
     * @return {@link BaseResponse}<{@link ProductInfo}>
     */
    @GetMapping("/get")
    public BaseResponse<ProductInfo> getProductInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = productInfoService.getById(id);
        return ResponseUtil.success(productInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param productInfoQueryRequest 商品信息查询请求
     * @return {@link BaseResponse}<{@link List}<{@link ProductInfo}>>
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @GetMapping("/list")
    public BaseResponse<List<ProductInfo>> listProductInfo(ProductInfoQueryRequest productInfoQueryRequest) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);

        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>(productInfoQuery);
        List<ProductInfo> productInfoList = productInfoService.list(queryWrapper);
        return ResponseUtil.success(productInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param productInfoQueryRequest 商品信息查询请求
     * @param request                 请求
     * @return {@link BaseResponse}<{@link Page}<{@link ProductInfo}>>
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<ProductInfo>> listProductInfoByPage(ProductInfoQueryRequest productInfoQueryRequest, HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }

        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        long size = productInfoQueryRequest.getPageSize();
        String sortField = productInfoQueryRequest.getSortField();
        String sortOrder = productInfoQueryRequest.getSortOrder();

        String name = productInfoQueryRequest.getName();
        long current = productInfoQueryRequest.getCurrent();
        String description = productInfoQueryRequest.getDescription();
        String productType = productInfoQueryRequest.getProductType();
        Integer addPoints = productInfoQueryRequest.getAddPoints();
        Integer total = productInfoQueryRequest.getTotal();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(StringUtils.isNotBlank(productType), "productType", productType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, size), queryWrapper);
        // 不是管理员只能查看已经上线的
        if (!userService.isAdmin(request)) {
            List<ProductInfo> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResponseUtil.success(productInfoPage);
    }

    /**
     * 分页获取列表
     *
     * @param productInfoQueryRequest 商品信息查询请求
     * @param request                 请求
     * @return {@link BaseResponse}<{@link Page}<{@link ProductInfo}>>
     */
    @GetMapping("/get/searchText")
    public BaseResponse<Page<ProductInfo>> listProductInfoBySearchTextPage(ProductInfoSearchTextRequest productInfoQueryRequest, HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);

        String searchText = productInfoQueryRequest.getSearchText();
        long size = productInfoQueryRequest.getPageSize();
        long current = productInfoQueryRequest.getCurrent();
        String sortField = productInfoQueryRequest.getSortField();
        String sortOrder = productInfoQueryRequest.getSortOrder();

        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchText), "name", searchText)
                .or()
                .like(StringUtils.isNotBlank(searchText), "description", searchText);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, size), queryWrapper);
        // 不是管理员只能查看已经上线的
        if (!userService.isAdmin(request)) {
            List<ProductInfo> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResponseUtil.success(productInfoPage);
    }

    /**
     * 发布
     *
     * @param idRequest id请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineProductInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductInfo productInfo = productInfoService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.ONLINE.getValue());
        return ResponseUtil.success(productInfoService.updateById(productInfo));
    }

    /**
     * 下线
     *
     * @param idRequest id请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> offlineProductInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductInfo productInfo = productInfoService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.OFFLINE.getValue());
        return ResponseUtil.success(productInfoService.updateById(productInfo));
    }
    // endregion
}
