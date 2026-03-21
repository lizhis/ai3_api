package com.ai.basewebsocket.controller;

import com.ai.basewebsocket.handler.TestMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author 
 *
 */
@RestController
public class TestMsgController {

    @Autowired
    private TestMsgHandler testMsgHandler;

    /**
     * 发送消息
     * @param param
     * @return
     * @throws Exception
     */
/*    @ApiOperation("发送消息")
    @PostMapping("/sendToUser")
    public boolean sendToUser(@RequestBody WsToUserParam param) throws Exception{
        return testMsgHandler.sendToUser(param);
    }*/
/*

    @RequestMapping("/sendToMarket")
    public boolean sendToMarket(@RequestBody WsToMarketParam param) throws Exception{
        return testMsgHandler.sendToMarket(param);
    }

    @RequestMapping("/sendToCommon")
    public boolean sendToCommon(@RequestBody WsToCommonParam param) throws Exception{
        return testMsgHandler.sendToCommon(param);
    }
*/


}
