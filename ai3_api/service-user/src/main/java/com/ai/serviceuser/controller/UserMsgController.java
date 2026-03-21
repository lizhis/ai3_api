package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.user.UserMsgParam;
import com.ai.basecommon.core.po.user.UserMsgPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.MyNewMsgVO;
import com.ai.basecommon.core.vo.user.UserMsgDetailVO;
import com.ai.basecommon.core.vo.user.UserMsgVO;
import com.ai.serviceuser.handler.UserMsgHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "站内信")
@RequestMapping("/userMsg")
public class UserMsgController {

    @Autowired
    private UserMsgHandler userMsgHandler;


    @Operation(summary = "查询列表",description = "")
    @GetMapping("/select")
    public BaseVO select(@ModelAttribute UserMsgParam param) throws Exception{
        List<UserMsgVO> result = userMsgHandler.select(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        UserMsgDetailVO result = userMsgHandler.detail(param);
        return BaseVO.ok(result);
    }

    @Operation(summary = "新消息",description = "")
    @GetMapping("/newMsg")
    public BaseVO newMsg() throws Exception{
        MyNewMsgVO result = userMsgHandler.newMsg();
        return BaseVO.ok(result);
    }








}
