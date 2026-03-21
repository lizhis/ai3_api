package com.ai.basewebsocket.consumer;

import com.ai.basecommon.core.dto.ws.WebSocketDTO;
import com.ai.basecommon.core.dto.ws.AiChatChatMsgDTO;
import com.ai.basecommon.enums.WsCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.basewebsocket.connect.WebSocketServer;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "ws_ai_chat",containerFactory = "wsAiChatContainerFactory")
public class WsAiChatConsumer {

    @RabbitHandler
    public void receive(String msg) {
        if(StringUtil.isEmpty(msg)){
            return;
        }
        //LogUtil.log("ws里接收到要发送给用户的ai消息：" + msg);

        AiChatChatMsgDTO msgDTO = null;
        try{
            msgDTO = JSONObject.parseObject(msg, AiChatChatMsgDTO.class);
        }catch (Exception e){
            LogUtil.log("mq消费消息失败：" + e.getMessage());
            return;
        }

        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setCode(WsCodeEnum.AI_CHAT.getCode());
        webSocketDTO.setContent(msgDTO.getContent());

        if(StringUtil.isEmpty(msgDTO.getDeviceId())){
            return;
        }
        WebSocketServer.sendDeviceId(msgDTO.getDeviceId(), webSocketDTO);
    }

}
