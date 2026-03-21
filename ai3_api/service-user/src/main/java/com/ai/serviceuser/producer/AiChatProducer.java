package com.ai.serviceuser.producer;

import com.ai.basecommon.core.dto.ws.AiChatChatMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AiChatProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(AiChatChatMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("ws_ai_chat", JSONObject.toJSONString(msgDTO));
    }



}
