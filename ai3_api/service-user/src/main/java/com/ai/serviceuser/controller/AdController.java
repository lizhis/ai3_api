package com.ai.serviceuser.controller;


import com.ai.basecommon.core.vo.BaseVO;
import com.ai.serviceuser.handler.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "广告")
@RequestMapping("/ad")
public class AdController {


    @Autowired
    private AdHandler adHandler;


    @Operation(summary = "激活归因",description = "")
    @PostMapping("/active")
    public BaseVO active() throws Exception{
        return adHandler.active();
    }

    @Operation(summary = "激活归因-巨量",description = "")
    @PostMapping("/activeOceanengine")
    public BaseVO activeOceanengine() throws Exception{
        return adHandler.activeOceanengine();
    }




}
