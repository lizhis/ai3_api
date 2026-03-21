package com.ai.serviceuser.controller;


import com.ai.basecommon.core.param.shop.ShopBuyParam;
import com.ai.basecommon.core.param.user.SeasonBuyParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.SysConfSeasonVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.SeasonHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "季卡")
@RequestMapping("/season")
public class SeasonController {


    @Autowired
    private SeasonHandler seasonHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "季卡配置",description = "")
    @GetMapping("/conf")
    public BaseVO conf() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfSeasonVO result = seasonHandler.conf();
        return BaseVO.ok(result);
    }


    @Operation(summary = "季卡开通",description = "")
    @PostMapping("/buy")
    public BaseVO buy(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody SeasonBuyParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = seasonHandler.buy(param);
        return BaseVO.bool(result);
    }

    @Operation(summary = "是否领取礼品",description = "")
    @GetMapping("/isGift")
    public BaseVO isGift(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = seasonHandler.isGift();
        return BaseVO.ok(result);
    }

    @Operation(summary = "领取礼品",description = "")
    @PostMapping("/gift")
    public BaseVO gift(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody ShopBuyParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = seasonHandler.gift(param);
        return BaseVO.bool(result);
    }





}
