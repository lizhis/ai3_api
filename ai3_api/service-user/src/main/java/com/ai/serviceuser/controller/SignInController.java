package com.ai.serviceuser.controller;

import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.SignRecordVO;
import com.ai.basecommon.core.vo.user.SysConfSignVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.SignInHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "签到")
@RequestMapping("/signIn")
public class SignInController {

    @Autowired
    private SignInHandler signInHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "获取签到配置",description = "")
    @GetMapping("/selectConf")
    public BaseVO selectConf() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfSignVO result = signInHandler.selectConf();
        return BaseVO.ok(result);
    }


    @Operation(summary = "获取本月签到记录",description = "")
    @GetMapping("/selectRecord")
    public BaseVO selectRecord() throws Exception{
        List<SignRecordVO> result = signInHandler.selectRecord();
        return BaseVO.ok(result);
    }

    @Operation(summary = "获取签到总次数",description = "")
    @GetMapping("/totalSign")
    public BaseVO totalSign() throws Exception{
        Integer result = signInHandler.totalSign();
        return BaseVO.ok(result);
    }


    @Operation(summary = "签到",description = "")
    @PostMapping("/signIn")
    public BaseVO signIn() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = signInHandler.signIn();
        return result;
    }



}
