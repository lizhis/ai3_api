package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.PayWayVO;
import com.ai.basecommon.core.vo.user.RechargeVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.RechargeHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "充值")
@RequestMapping("/recharge")
public class RechargeController {

    @Autowired
    private RechargeHandler rechargeHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "获取充值方式",description = "")
    @GetMapping("/payWay")
    public BaseVO payWay() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<PayWayVO> result = rechargeHandler.payWay();
        return BaseVO.ok(result);
    }


    @Operation(summary = "银行卡充值",description = "")
    @PostMapping("/bankPay")
    public BaseVO bankPay(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody RechargeBankPayParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.bankPay(param);
        return result;
    }


    @Operation(summary = "充值记录",description = "")
    @GetMapping("/select")
    public BaseVO select(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @ModelAttribute RechargeRecordParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<RechargeVO> result = rechargeHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "支付宝充值",description = "")
    @PostMapping("/alipay")
    public BaseVO alipay(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeAlipayParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.alipay(param);
        return result;
    }

    @Operation(summary = "支付宝当面付充值",description = "")
    @PostMapping("/alipayScan")
    public BaseVO alipayScan(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeAlipayScanParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.alipayScan(param);
        return result;
    }

/*

    @Operation(summary = "财源支付充值",description = "")
    @PostMapping("/caiyuan")
    public BaseVO caiyuan(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeCaiyuanParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.caiyuan(param);
        return result;
    }
*/


    @Operation(summary = "长卿支付充值",description = "")
    @PostMapping("/changqing")
    public BaseVO changqing(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeChangqingParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.changqing(param);
        return result;
    }
/*

    @Operation(summary = "智汇支付充值",description = "")
    @PostMapping("/zhihui")
    public BaseVO zhihui(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeZhihuiParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.zhihui(param);
        return result;
    }
*/
/*

    @Operation(summary = "华达支付充值",description = "")
    @PostMapping("/huada")
    public BaseVO huada(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeHuadaParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.huada(param);
        return result;
    }
*/

    @Operation(summary = "华达2支付充值",description = "")
    @PostMapping("/huada2")
    public BaseVO huada(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeHuada2Param param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.huada2(param);
        return result;
    }

    @Operation(summary = "麒麟支付充值",description = "")
    @PostMapping("/qilin")
    public BaseVO qilin(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody RechargeQilinParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = rechargeHandler.qilin(param);
        return result;
    }







}
