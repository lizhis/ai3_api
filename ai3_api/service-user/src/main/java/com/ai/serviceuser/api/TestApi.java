package com.ai.serviceuser.api;

import com.ai.basecommon.utils.LogUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test",produces = "application/json;charset=utf-8")
public class TestApi {


    //@RequestMapping(value = "/str",produces = "application/json;charset=utf-8")
    @RequestMapping("/str")
    public String str() throws Exception{
        String st = "我是用户服务里出来的";
        LogUtil.log("打印：" + st);
        return st;
    }

}
