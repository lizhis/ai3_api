package com.ai.basead.api;

import com.ai.basead.handler.AdHandler;
import com.ai.basead.handler.OceanengineHandler;
import com.ai.basecommon.core.dto.ad.AdVerifyParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/adApi",produces = "application/json;charset=utf-8")
public class AdApi {

    @Autowired
    private AdHandler adHandler;

    @Autowired
    private OceanengineHandler oceanengineHandler;

    //激活
    @RequestMapping("/active")
    public void active(@RequestBody String id) throws Exception{
        adHandler.active(id);
    }


    //激活-巨量渠道
    @RequestMapping("/oceanengineActive")
    public void oceanengineActive(@RequestBody AdVerifyParamDTO paramDTO) throws Exception{
        oceanengineHandler.active(paramDTO);
    }


    //转化验证
    @RequestMapping("/verify")
    public Integer verify(@RequestBody AdVerifyParamDTO paramDTO) throws Exception{
        return adHandler.verify(paramDTO);
    }

    //留存
    @RequestMapping("/remain")
    public void remain(@RequestBody String deviceId) throws Exception{
        adHandler.remain(deviceId);
    }








}
