package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.step.ClockParam;
import com.ai.basecommon.core.param.step.StepReportParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.step.MyClockVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.StepGoldHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "步步生金")
@RequestMapping("/stepGold")
public class StepGoldController {

    @Autowired
    private StepGoldHandler stepGoldHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "获取我的打卡次数",description = "")
    @GetMapping("/myClock")
    public BaseVO myClock(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        MyClockVO result = stepGoldHandler.myClock();
        return BaseVO.ok(result);
    }


    @Operation(summary = "打卡",description = "")
    @PostMapping("/clock")
    public BaseVO clock(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody ClockParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = stepGoldHandler.clock(param);
        return result;
    }

    @Operation(summary = "步数上报",description = "")
    @PostMapping("/report")
    public BaseVO report(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody StepReportParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = stepGoldHandler.report(param);
        return result;
    }








}
