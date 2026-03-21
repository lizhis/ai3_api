package com.ai.servicebase.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.base.GuideParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.GuideCateVO;
import com.ai.basecommon.core.vo.base.GuideVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebase.commom.SignatureUtilX;
import com.ai.servicebase.commom.UserUtilX;
import com.ai.servicebase.handler.GuideHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "新手指南")
@RequestMapping("/guide")
public class GuideController {

    @Autowired
    private GuideHandler guideHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询分类",description = "")
    @GetMapping("/selectCateList")
    public BaseVO selectCateList() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<GuideCateVO> result = guideHandler.selectCateList();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询列表",description = "")
    @GetMapping("/select")
    public BaseVO select(@ModelAttribute GuideParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<GuideVO> result = guideHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        GuideVO result = guideHandler.findVOById(param);
        return BaseVO.ok(result);
    }








}
