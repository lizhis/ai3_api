package com.ai.servicebusiness.controller;


import com.ai.basecommon.core.param.shop.GiftReceiveParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.shop.ShopVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebusiness.commom.SignatureUtilX;
import com.ai.servicebusiness.commom.UserUtilX;
import com.ai.servicebusiness.handler.GiftHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "免费礼品")
@RequestMapping("/gift")
public class GiftController {

    @Autowired
    private GiftHandler giftHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询免费礼品",description = "")
    @GetMapping("/selectGift")
    public BaseVO selectGift() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<ShopVO> result = giftHandler.selectGift();
        return BaseVO.ok(result);
    }


    @Operation(summary = "校验福利码",description = "")
    @PostMapping("/checkCode")
    public BaseVO checkCode(@RequestBody GiftReceiveParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = giftHandler.checkCode(param);
        return result;
    }


    @Operation(summary = "领取",description = "")
    @PostMapping("/receive")
    public BaseVO receive(@RequestBody GiftReceiveParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = giftHandler.receive(param);
        return result;
    }



}
