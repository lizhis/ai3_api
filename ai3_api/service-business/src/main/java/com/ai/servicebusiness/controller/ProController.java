package com.ai.servicebusiness.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.pro.ProOrderAddParam;
import com.ai.basecommon.core.param.pro.MyProListParam;
import com.ai.basecommon.core.param.pro.ProParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.pro.ProContractVO;
import com.ai.basecommon.core.vo.pro.ProVO;
import com.ai.basecommon.core.vo.pro.MyProDetailVO;
import com.ai.basecommon.core.vo.pro.MyProVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebusiness.commom.SignatureUtilX;
import com.ai.servicebusiness.handler.ProHandler;
import com.ai.servicebusiness.handler.ProOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "项目")
@RequestMapping("/pro")
public class ProController {

    @Autowired
    private ProHandler proHandler;

    @Autowired
    private ProOrderHandler proOrderHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询",description = "")
    @GetMapping("/select")
    public BaseVO select(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@ModelAttribute ProParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = proHandler.select(param);
        return result;
    }

    @Operation(summary = "查询首页推荐",description = "")
    @GetMapping("/selectHome")
    public BaseVO selectHome(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = proHandler.selectHome();
        return result;
    }

    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@ModelAttribute IdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ProVO result = proHandler.detail(param.getId());
        return BaseVO.ok(result);
    }


    @Operation(summary = "项目投放",description = "")
    @PostMapping("/put")
    public BaseVO put(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody ProOrderAddParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = proOrderHandler.put(param);
        return result;
    }


    @Operation(summary = "我的项目",description = "")
    @GetMapping("/myList")
    public BaseVO myList(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@ModelAttribute MyProListParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<MyProVO> result = proOrderHandler.myList(param);
        return BaseVO.ok(result);
    }

    @Operation(summary = "我的项目详情",description = "")
    @GetMapping("/myDetail")
    public BaseVO myDetail(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@ModelAttribute OrderIdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        MyProDetailVO result = proOrderHandler.myDetail(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "合同",description = "")
    @GetMapping("/contract")
    public BaseVO contract(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@ModelAttribute OrderIdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        ProContractVO result = proOrderHandler.contract(param);
        return BaseVO.ok(result);
    }





}
