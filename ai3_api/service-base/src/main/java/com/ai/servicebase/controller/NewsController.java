package com.ai.servicebase.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.po.base.article.NewsPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.NewsDetailVO;
import com.ai.basecommon.core.vo.base.NewsVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebase.commom.SignatureUtilX;
import com.ai.servicebase.commom.UserUtilX;
import com.ai.servicebase.handler.NewsHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "新闻")
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsHandler newsHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询列表",description = "")
    @GetMapping("/select")
    public BaseVO select(@ModelAttribute PageIn param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<NewsVO> result = newsHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        NewsDetailVO result = newsHandler.findById(param);
        return BaseVO.ok(result);
    }








}
