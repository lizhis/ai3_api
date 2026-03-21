package com.ai.basead.controller;

import com.ai.basead.handler.BaiduHandler;
import com.ai.basecommon.core.param.ad.AdBaiduParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "百度广告")
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduHandler baiduHandler;

    @Operation(summary = "监测",description = "")
    @GetMapping("/listen")
    public void listen(@ModelAttribute AdBaiduParam param) throws Exception{
        baiduHandler.listen(param);
    }



}
