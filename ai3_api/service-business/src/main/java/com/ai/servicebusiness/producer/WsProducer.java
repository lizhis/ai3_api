package com.ai.servicebusiness.producer;

import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WsProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(WsSendDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("ws", JSONObject.toJSONString(msgDTO));
    }



}
