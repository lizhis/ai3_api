package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.*;
import com.ai.basecommon.core.po.user.UserAlipayPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.MyChildrenVO;
import com.ai.basecommon.core.vo.user.SysVipVO;
import com.ai.basecommon.core.vo.user.UserBankcardVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.InviteLevelHandler;
import com.ai.serviceuser.handler.SysVipHandler;
import com.ai.serviceuser.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "用户管理")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserHandler userHandler;

    @Autowired
    private SysVipHandler sysVipHandler;

    @Autowired
    private InviteLevelHandler inviteLevelHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "修改密码",description = "")
    @PostMapping("/editPassword")
    public BaseVO editPassword(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody EditPasswordParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = userHandler.editPassword(param);
        return BaseVO.bool(result);
    }

    @Operation(summary = "修改交易密码",description = "")
    @PostMapping("/editPayPassword")
    public BaseVO editPayPassword(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody EditPayPasswordParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = userHandler.editPayPassword(param);
        return BaseVO.bool(result);
    }

    @Operation(summary = "实名认证",description = "")
    @PostMapping("/auth")
    public BaseVO auth(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody UserAuthParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = userHandler.auth(param);
        return result;
    }

    @Operation(summary = "更新头像",description = "")
    @PostMapping("/editPortrait")
    public BaseVO editPortrait(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, MultipartFile file) throws Exception {
        //LogUtil.log("更新用户头像 正在上传：" + file.getOriginalFilename());
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        String result = userHandler.editPortrait(file);
        return BaseVO.ok(result);
    }


    @Operation(summary = "获取vip等级配置",description = "")
    @GetMapping("/vipList")
    public BaseVO vipList() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<SysVipVO> result = sysVipHandler.vipList();
        return BaseVO.ok(result);
    }

    @Operation(summary = "获取我的银行卡",description = "")
    @GetMapping("/myBankCard")
    public BaseVO myBankCard() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        UserBankcardVO result = userHandler.myBankCard();
        return BaseVO.ok(result);
    }

    @Operation(summary = "绑定银行卡",description = "")
    @PostMapping("/bindCard")
    public BaseVO bindCard(@RequestBody BindCardParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = userHandler.bindCard(param);
        return BaseVO.bool(result);
    }


    @Operation(summary = "我的一级下线",description = "")
    @GetMapping("/myChildren1")
    public BaseVO myChildren1() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        MyChildrenVO result = inviteLevelHandler.myChildren1();
        return BaseVO.ok(result);
    }


    @Operation(summary = "获取我的支付宝",description = "")
    @GetMapping("/myAlipay")
    public BaseVO myAlipay() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        UserAlipayPO result = userHandler.myAlipay();
        return BaseVO.ok(result);
    }

    @Operation(summary = "绑定支付宝",description = "")
    @PostMapping("/bindAlipay")
    public BaseVO bindAlipay(@RequestBody BindAlipayParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = userHandler.bindAlipay(param);
        return BaseVO.bool(result);
    }


}
