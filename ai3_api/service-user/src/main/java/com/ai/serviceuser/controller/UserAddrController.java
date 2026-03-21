package com.ai.serviceuser.controller;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.user.addr.UserAddrAddParam;
import com.ai.basecommon.core.param.user.addr.UserAddrEditParam;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserAddrVO;
import com.ai.serviceuser.handler.UserAddrHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "收货地址管理")
@RequestMapping("/userAddr")
public class UserAddrController {

    @Autowired
    private UserAddrHandler userAddrHandler;


    @Operation(summary = "查询",description = "")
    @GetMapping("/select")
    public BaseVO select(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token) throws Exception{
        List<UserAddrVO> result = userAddrHandler.select();
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询详情",description = "")
    @GetMapping("/detail")
    public BaseVO detail(@ModelAttribute IdParam param) throws Exception{
        UserAddrVO result = userAddrHandler.detail(param.getId());
        return BaseVO.ok(result);
    }


    @Operation(summary = "查询默认地址",description = "")
    @GetMapping("/defaultAddr")
    public BaseVO defaultAddr() throws Exception{
        UserAddrVO result = userAddrHandler.defaultAddr();
        return BaseVO.ok(result);
    }



    @Operation(summary = "添加收货地址",description = "")
    @PostMapping("/add")
    public BaseVO add(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody UserAddrAddParam param) throws Exception{
        boolean result = userAddrHandler.add(param);
        return BaseVO.bool(result);
    }

    @Operation(summary = "更新收货地址",description = "")
    @PostMapping("/edit")
    public BaseVO edit(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token,@RequestBody UserAddrEditParam param) throws Exception{
        boolean result = userAddrHandler.edit(param);
        return BaseVO.bool(result);
    }




}
