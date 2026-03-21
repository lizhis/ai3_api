package com.ai.serviceuser.controller;

import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.handler.UploadHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "上传")
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadHandler uploadHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "上传文件",description = "")
    @PostMapping("/file")
    public BaseVO file(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, MultipartFile file) throws Exception {
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        String result = uploadHandler.file(file);
        return BaseVO.ok(result);
    }


}
