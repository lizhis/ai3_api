package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.entrance.ForgetParam;
import com.ai.basecommon.core.param.entrance.LoginParam;
import com.ai.basecommon.core.param.entrance.RegisterParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserInfoVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.EntranceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author
 */
@RestController
@Tag(name = "用户入口")
@RequestMapping("/entrance")
public class EntranceController {

    @Autowired
    private EntranceHandler entranceHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "注册",description = "")
    @PostMapping("/register")
    public BaseVO register(@RequestBody RegisterParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = entranceHandler.register(param);
        return result;
    }


    @Operation(summary = "登录",description = "")
    @PostMapping("/login")
    public BaseVO login(@RequestBody LoginParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = entranceHandler.login(param);
        return result;
    }

    @Operation(summary = "忘记密码",description = "")
    @PostMapping("/forget")
    public BaseVO forget(@RequestBody ForgetParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = entranceHandler.forget(param);
        return result;
    }


    @GetMapping("/userInfo")
    @Operation(summary = "拉取用户信息")
    public BaseVO userInfo(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        UserInfoVO result = entranceHandler.userInfo();
        return BaseVO.ok(result);
    }



}
