package com.ai.basead.controller;

import com.ai.basead.handler.BianxianmaoHandler;
import com.ai.basecommon.core.param.ad.AdBianxianmaoParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "变现猫广告")
@RequestMapping("/bianxianmao")
public class BianxianmaoController {

    @Autowired
    private BianxianmaoHandler bianxianmaoHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public String listen(@ModelAttribute AdBianxianmaoParam param) throws Exception{
        bianxianmaoHandler.listen(param);
        return "OK";
    }



}
