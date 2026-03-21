package com.ai.serviceuser.api;

import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.serviceuser.handler.BaseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/baseApi",produces = "application/json;charset=utf-8")
public class BaseApi {

    @Autowired
    private BaseHandler baseHandler;


    //查询第三方配置
    @RequestMapping("/findSysConfApi")
    public SysConfApiPO findSysConfApi() throws Exception{
        return baseHandler.findSysConfApi();
    }








}
