package com.ai.servicebase.controller;

import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.param.base.AIChatParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebase.commom.SignatureUtilX;
import com.ai.servicebase.handler.AIChatHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "AI聊天")
@RequestMapping("/AIChat")
public class AIChatController {

    @Autowired
    private AIChatHandler aiChatHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "聊天",description = "")
    @PostMapping("/send")
    public BaseVO send(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestBody AIChatParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        BaseVO result = aiChatHandler.send(param);
        return result;
    }

    @Operation(summary = "清除会话",description = "")
    @PostMapping("/cleanHistory")
    public BaseVO cleanHistory(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        BaseVO result = aiChatHandler.cleanHistory();
        return result;
    }


    @Operation(summary = "查询聊天记录",description = "")
    @GetMapping("/history")
    public BaseVO history() throws Exception{
        List<AIChatMessageDTO> result = aiChatHandler.history();
        return BaseVO.ok(result);
    }







}
