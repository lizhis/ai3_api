package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.GoldExchangeParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserBalanceVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.handler.BalanceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "余额管理")
@RequestMapping("/balance")
public class BalanceController {

    @Autowired
    private BalanceHandler balanceHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "余额信息",description = "")
    @GetMapping("/info")
    public BaseVO info() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        UserBalanceVO result = balanceHandler.info();
        return BaseVO.ok(result);
    }

    @Operation(summary = "云豆提取",description = "")
    @PostMapping("/goldExchange")
    public BaseVO goldExchange(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody GoldExchangeParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = balanceHandler.goldExchange(param);
        return result;
    }


}
