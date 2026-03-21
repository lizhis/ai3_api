package com.ai.basead.controller;

import com.ai.basead.handler.TencentHandler;
import com.ai.basecommon.core.param.ad.AdTencentParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "腾讯广告")
@RequestMapping("/tencent")
public class TencentController {

    @Autowired
    private TencentHandler tencentHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdTencentParam param) throws Exception{
        tencentHandler.listen(param);
    }



}
