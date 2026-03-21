package com.ai.servicebase.controller;

import com.ai.basecommon.core.vo.base.AgreementVO;
import com.ai.servicebase.handler.AgreementHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Tag(name = "H5页面")
@RequestMapping("/h5")
public class H5Controller {

    @Autowired
    private AgreementHandler agreementHandler;


    @Operation(summary = "用户协议",description = "")
    @GetMapping("/userAgreement")
    public String userAgreement(Model model) throws Exception{
        AgreementVO result = agreementHandler.userAgreement();
        model.addAttribute("vo",result);
        return "/agreement/userAgreement";
    }

    @Operation(summary = "隐私政策",description = "")
    @GetMapping("/privacyAgreement")
    public String privacyAgreement(Model model) throws Exception{
        AgreementVO result = agreementHandler.privacyAgreement();
        model.addAttribute("vo",result);
        return "/agreement/privacyAgreement";
    }


    @Operation(summary = "应用权限",description = "")
    @GetMapping("/appPermissions")
    public String appPermissions(Model model) throws Exception{
        AgreementVO result = agreementHandler.appPermissions();
        model.addAttribute("vo",result);
        return "/agreement/appPermissions";
    }










}
