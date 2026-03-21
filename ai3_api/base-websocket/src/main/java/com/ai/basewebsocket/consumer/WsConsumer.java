package com.ai.basewebsocket.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.ws.WebSocketDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.basewebsocket.connect.WebSocketServer;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//,concurrency = "5-10"
@Component
@RabbitListener(queues = "ws",containerFactory = "wsContainerFactory")
public class WsConsumer {

    @RabbitHandler
    public void receive(String msg) {
        if(StringUtil.isEmpty(msg)){
            return;
        }
        LogUtil.log("ws里的mq消费消息：" + msg);

        WsSendDTO msgDTO = null;
        try{
            msgDTO = JSONObject.parseObject(msg, WsSendDTO.class);
        }catch (Exception e){
            LogUtil.log("mq消费消息失败：" + e.getMessage());
            return;
        }


        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setCode(msgDTO.getCode());
        webSocketDTO.setContent(msgDTO.getContent());
        webSocketDTO.setMark(msgDTO.getMark());

        if(null == webSocketDTO.getCode()){
            webSocketDTO.setCode(0);
        }
        if(null == webSocketDTO.getContent()){
            webSocketDTO.setContent("");
        }
        if(null == webSocketDTO.getMark()){
            webSocketDTO.setMark("");
        }

        if(null != msgDTO.getUserId() && msgDTO.getUserId() > 0){
            LogUtil.log("发送给指定用户：" + msgDTO.getUserId());
            WebSocketServer.sendUser(msgDTO.getUserId(), webSocketDTO);
        }
        else if(!StringUtil.isEmpty(msgDTO.getDeviceId())){
            LogUtil.log("发送给指定设备：" + msgDTO.getDeviceId());
            WebSocketServer.sendDeviceId(msgDTO.getDeviceId(), webSocketDTO);
        }
        else{
            LogUtil.log("发送给全体用户");
            WebSocketServer.sendAll(webSocketDTO);
        }
    }

}
