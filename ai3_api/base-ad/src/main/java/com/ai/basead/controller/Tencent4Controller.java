package com.ai.basead.controller;

import com.ai.basead.handler.Tencent4Handler;
import com.ai.basecommon.core.param.ad.AdTencentParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "腾讯广告4")
@RequestMapping("/tencent4")
public class Tencent4Controller {

    @Autowired
    private Tencent4Handler tencent4Handler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdTencentParam param) throws Exception{
        tencent4Handler.listen(param);
    }



}
