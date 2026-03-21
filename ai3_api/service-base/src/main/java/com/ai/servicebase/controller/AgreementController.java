package com.ai.servicebase.controller;

import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.AgreementVO;
import com.ai.servicebase.handler.AgreementHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "协议")
@RequestMapping("/agreement")
public class AgreementController {

    @Autowired
    private AgreementHandler agreementHandler;

    @Operation(summary = "用户协议",description = "")
    @GetMapping("/userAgreement")
    public BaseVO userAgreement() throws Exception{
        AgreementVO result = agreementHandler.userAgreement();
        return BaseVO.ok(result);
    }

    @Operation(summary = "隐私政策",description = "")
    @GetMapping("/privacyAgreement")
    public BaseVO privacyAgreement() throws Exception{
        AgreementVO result = agreementHandler.privacyAgreement();
        return BaseVO.ok(result);
    }

    @Operation(summary = "应用权限",description = "")
    @GetMapping("/appPermissions")
    public BaseVO appPermissions() throws Exception{
        AgreementVO result = agreementHandler.appPermissions();
        return BaseVO.ok(result);
    }

    @Operation(summary = "季卡plus协议",description = "")
    @GetMapping("/seasonAgreement")
    public BaseVO seasonAgreement() throws Exception{
        AgreementVO result = agreementHandler.seasonAgreement();
        return BaseVO.ok(result);
    }









}
