package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.user.UserAssetTrendsParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserAssetTrendsVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.UserAssetTrendsHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "资产动态")
@RequestMapping("/userAssetTrends")
public class UserAssetTrendsController {

    @Autowired
    private UserAssetTrendsHandler userAssetTrendsHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "资产动态",description = "")
    @GetMapping("/select")
    public BaseVO select(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @ModelAttribute UserAssetTrendsParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<UserAssetTrendsVO> result = userAssetTrendsHandler.select(param);
        return BaseVO.ok(result);
    }


}
