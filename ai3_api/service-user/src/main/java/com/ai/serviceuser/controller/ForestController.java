package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.forest.ForestCareParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.forest.ForestCareVO;
import com.ai.basecommon.core.vo.forest.ForestTreeVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.ForestHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "云币森林")
@RequestMapping("/forest")
public class ForestController {

    @Autowired
    private ForestHandler forestHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "获取我的树",description = "")
    @GetMapping("/myTree")
    public BaseVO myTree(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        ForestTreeVO result = forestHandler.myTree();
        return BaseVO.ok(result);
    }


    @Operation(summary = "养护",description = "")
    @PostMapping("/care")
    public BaseVO care(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody ForestCareParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ForestCareVO result = forestHandler.care(param);
        return BaseVO.ok(result);
    }








}
