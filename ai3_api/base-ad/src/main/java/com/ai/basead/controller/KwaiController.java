package com.ai.basead.controller;

import com.ai.basead.handler.KwaiHandler;
import com.ai.basecommon.core.param.ad.AdKwaiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "快手广告")
@RequestMapping("/kwai")
public class KwaiController {

    @Autowired
    private KwaiHandler kwaiHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdKwaiParam param) throws Exception{
        kwaiHandler.listen(param);
    }



}
