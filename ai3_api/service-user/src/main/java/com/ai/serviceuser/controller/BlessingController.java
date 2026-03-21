package com.ai.serviceuser.controller;


import com.ai.basecommon.core.param.base.BlessingCardDetailParam;
import com.ai.basecommon.core.po.base.SysConfBlessingPO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserBlessingVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.BlessingHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "集福")
@RequestMapping("/blessing")
public class BlessingController {


    @Autowired
    private BlessingHandler blessingHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "获取集福配置",description = "")
    @GetMapping("/findConf")
    public BaseVO findConf() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfBlessingPO result = blessingHandler.findConf();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询福卡详情",description = "")
    @GetMapping("/findCardDetail")
    public BaseVO findCardDetail(@ModelAttribute BlessingCardDetailParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        String result = blessingHandler.findCardDetail(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询我的福卡",description = "")
    @GetMapping("/myBlessing")
    public BaseVO myBlessing(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        UserBlessingVO result = blessingHandler.myBlessing();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询我的邀请卡",description = "")
    @GetMapping("/myInviteBlessing")
    public BaseVO myInviteBlessing(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        Integer result = blessingHandler.myInviteBlessing();
        return BaseVO.ok(result);
    }



}
