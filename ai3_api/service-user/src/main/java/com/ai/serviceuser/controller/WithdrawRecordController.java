package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.WithdrawRecordParam;
import com.ai.basecommon.core.po.user.WithdrawRecordPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.WithdrawRecordHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "提现记录")
@RequestMapping("/withdrawRecord")
public class WithdrawRecordController {

    @Autowired
    private WithdrawRecordHandler withdrawRecordHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "提现记录",description = "")
    @GetMapping("/select")
    public BaseVO select(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @ModelAttribute WithdrawRecordParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<WithdrawRecordPO> result = withdrawRecordHandler.select(param);
        return BaseVO.ok(result);
    }


}
