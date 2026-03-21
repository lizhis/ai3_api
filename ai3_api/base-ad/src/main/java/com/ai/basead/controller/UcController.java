package com.ai.basead.controller;

import com.ai.basead.handler.UcHandler;
import com.ai.basecommon.core.param.ad.AdUcParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "超级汇川")
@RequestMapping("/uc")
public class UcController {

    @Autowired
    private UcHandler ucHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdUcParam param) throws Exception{
        ucHandler.listen(param);
    }



}
