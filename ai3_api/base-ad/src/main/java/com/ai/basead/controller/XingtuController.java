package com.ai.basead.controller;

import com.ai.basead.handler.XingtuHandler;
import com.ai.basecommon.core.param.ad.AdXingtuParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "巨量星图")
@RequestMapping("/xingtu")
public class XingtuController {

    @Autowired
    private XingtuHandler xingtuHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdXingtuParam param) throws Exception{
        xingtuHandler.listen(param);
    }



}
