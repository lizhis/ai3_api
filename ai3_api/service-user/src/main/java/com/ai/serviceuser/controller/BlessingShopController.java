package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.blessingshop.BlessingShopBuyParam;
import com.ai.basecommon.core.param.blessingshop.BlessingShopParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.BlessingShopCateVO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderDetailVO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderVO;
import com.ai.basecommon.core.vo.user.BlessingShopVO;
import com.ai.serviceuser.handler.BlessingShopHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "福卡商城")
@RequestMapping("/blessingShop")
public class BlessingShopController {

    @Autowired
    private BlessingShopHandler blessingShopHandler;


    @Operation(summary = "查询商城类目",description = "")
    @GetMapping("/selectCateList")
    public BaseVO selectCateList() throws Exception{
        List<BlessingShopCateVO> result = blessingShopHandler.selectCateList();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询商品列表",description = "")
    @GetMapping("/select")
    public BaseVO select(@ModelAttribute BlessingShopParam param) throws Exception{
        List<BlessingShopVO> result = blessingShopHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        BlessingShopVO result = blessingShopHandler.detail(param.getId());
        return BaseVO.ok(result);
    }


    @Operation(summary = "下单",description = "")
    @PostMapping("/buy")
    public BaseVO buy(@RequestBody BlessingShopBuyParam param) throws Exception{
        BaseVO result = blessingShopHandler.buy(param);
        return result;
    }


    @Operation(summary = "查询我的订单",description = "")
    @GetMapping("/myOrder")
    public BaseVO myOrder() throws Exception{
        List<BlessingShopOrderVO> result = blessingShopHandler.myOrder();
        return BaseVO.ok(result);
    }


    @Operation(summary = "确认收货",description = "")
    @PostMapping("/orderConfirm")
    public BaseVO orderConfirm(@RequestBody OrderIdParam param) throws Exception{
        boolean result = blessingShopHandler.orderConfirm(param);
        return BaseVO.bool(result);
    }


    @Operation(summary = "查询订单详情",description = "")
    @GetMapping("/orderDetail")
    public BaseVO orderDetail(@ModelAttribute OrderIdParam param) throws Exception{
        BlessingShopOrderDetailVO result = blessingShopHandler.orderDetail(param);
        return BaseVO.ok(result);
    }



}
