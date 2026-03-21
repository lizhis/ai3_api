package com.ai.servicebase.controller;

import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.SysBankVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebase.commom.SignatureUtilX;
import com.ai.servicebase.handler.SysBankHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "银行配置")
@RequestMapping("/sysBank")
public class SysBankController {

    @Autowired
    private SysBankHandler sysBankHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询列表",description = "")
    @GetMapping("/select")
    public BaseVO select() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<SysBankVO> result = sysBankHandler.select();
        return BaseVO.ok(result);
    }







}
