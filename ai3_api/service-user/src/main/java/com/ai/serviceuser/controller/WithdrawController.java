package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.WithdrawParam;
import com.ai.basecommon.core.param.user.WithdrawQuotaParam;
import com.ai.basecommon.core.param.user.WithdrawRecordParam;
import com.ai.basecommon.core.po.user.WithdrawPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.SysConfWithdrawQuotaVO;
import com.ai.basecommon.core.vo.base.SysConfWithdrawVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.WithdrawHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "提现")
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private WithdrawHandler withdrawHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "获取提现配置",description = "")
    @GetMapping("/sysConfWithdraw")
    public BaseVO sysConfWithdraw(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfWithdrawVO result = withdrawHandler.sysConfWithdraw();
        return BaseVO.ok(result);
    }

    @Operation(summary = "提现",description = "")
    @PostMapping("/withdraw")
    public BaseVO withdraw(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody WithdrawParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = withdrawHandler.withdraw(param);
        return result;
    }



    @Operation(summary = "获取小额提现配置",description = "")
    @GetMapping("/sysConfWithdrawQuota")
    public BaseVO sysConfWithdrawQuota(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfWithdrawQuotaVO result = withdrawHandler.sysConfWithdrawQuota();
        return BaseVO.ok(result);
    }


    @Operation(summary = "小额提现",description = "")
    @PostMapping("/withdrawQuota")
    public BaseVO withdrawQuota(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody WithdrawQuotaParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = withdrawHandler.withdrawQuota(param);
        return result;
    }



}
