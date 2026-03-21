package com.ai.serviceuser.controller;

import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.param.base.AIChatParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.handler.AIHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

@RestController
@Tag(name = "AI聊天")
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIHandler aiHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;


    @Operation(summary = "查询聊天记录",description = "")
    @GetMapping("/history")
    public BaseVO history() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        List<AIChatMessageDTO> result = aiHandler.history();
        return BaseVO.ok(result);
    }

    @Operation(summary = "聊天",description = "")
    @PostMapping(value = "/chat",produces = "text/event-stream;charset=UTF-8")
    public SseEmitter chat(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, HttpServletResponse response, @RequestBody AIChatParam param) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            BaseException.error(statusCodeEnum);
        }
        response.setContentType("text/event-stream; charset=UTF-8");
        return aiHandler.chat(param);
    }



/*

    @Operation(summary = "清除会话",description = "")
    @PostMapping("/cleanHistory")
    public BaseVO cleanHistory(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        BaseVO result = aiChatHandler.cleanHistory();
        return result;
    }
*/

/*


    @Operation(summary = "查询聊天记录",description = "")
    @GetMapping("/history")
    public BaseVO history() throws Exception{
        List<AIChatMessageDTO> result = aiChatHandler.history();
        return BaseVO.ok(result);
    }
*/







}
