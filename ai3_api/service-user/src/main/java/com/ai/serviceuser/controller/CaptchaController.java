package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.captcha.YzmForgetParam;
import com.ai.basecommon.core.param.captcha.YzmRegisterParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.CaptchaHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author
 */
@RestController
@Tag(name = "验证码")
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaHandler captchaHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "发送注册验证码",description = "")
    @PostMapping("/register")
    public BaseVO register(@RequestBody YzmRegisterParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = captchaHandler.register(param);
        return result;
    }


    @Operation(summary = "发送忘记密码短信",description = "")
    @PostMapping("/forget")
    public BaseVO forget(@RequestBody YzmForgetParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = captchaHandler.forget(param);
        return result;
    }


}
