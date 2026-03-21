package com.ai.basead.controller;

import com.ai.basead.handler.OceanengineHandler;
import com.ai.basecommon.core.param.ad.AdOceanengineParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "巨量引擎")
@RequestMapping("/oceanengine")
public class OceanengineController {

    @Autowired
    private OceanengineHandler oceanengineHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdOceanengineParam param) throws Exception{
        oceanengineHandler.listen(param);
    }



}
