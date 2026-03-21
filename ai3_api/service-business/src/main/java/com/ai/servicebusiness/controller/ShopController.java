package com.ai.servicebusiness.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.shop.MyOrderParam;
import com.ai.basecommon.core.param.shop.ShopBuyParam;
import com.ai.basecommon.core.param.shop.ShopParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.shop.*;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebusiness.commom.SignatureUtilX;
import com.ai.servicebusiness.handler.ShopHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "商城")
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopHandler shopHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "查询商城类目",description = "")
    @GetMapping("/selectCateList")
    public BaseVO selectCateList() throws Exception{
        List<ShopCateVO> result = shopHandler.selectCateList();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询商品列表",description = "")
    @GetMapping("/select")
    public BaseVO select(@ModelAttribute ShopParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<ShopVO> result = shopHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询全部商品",description = "")
    @GetMapping("/selectAll")
    public BaseVO selectAll() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ShopAllVO result = shopHandler.selectAll();
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ShopDetailVO result = shopHandler.detail(param.getId());
        return BaseVO.ok(result);
    }


    @Operation(summary = "下单",description = "")
    @PostMapping("/buy")
    public BaseVO buy(@RequestBody ShopBuyParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = shopHandler.buy(param);
        return result;
    }


    @Operation(summary = "查询我的订单",description = "")
    @GetMapping("/myOrder")
    public BaseVO myOrder(@ModelAttribute MyOrderParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<ShopOrderVO> result = shopHandler.myOrder(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "确认收货",description = "")
    @PostMapping("/orderConfirm")
    public BaseVO orderConfirm(@RequestBody OrderIdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = shopHandler.orderConfirm(param);
        return BaseVO.bool(result);
    }


    @Operation(summary = "查询订单详情",description = "")
    @GetMapping("/orderDetail")
    public BaseVO orderDetail(@ModelAttribute OrderIdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ShopOrderDetailVO result = shopHandler.orderDetail(param);
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询商品确认详情",description = "")
    @GetMapping("/findShopConfirm")
    public BaseVO findShopConfirm(@ModelAttribute IdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ShopOrderConfirmVO result = shopHandler.findShopConfirm(param.getId());
        return BaseVO.ok(result);
    }


}
