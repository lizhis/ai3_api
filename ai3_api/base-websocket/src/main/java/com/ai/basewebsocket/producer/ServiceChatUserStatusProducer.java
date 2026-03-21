package com.ai.basewebsocket.producer;

import com.ai.basecommon.core.dto.msg.ServiceChatUserStatusMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceChatUserStatusProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(ServiceChatUserStatusMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("service_chat_user_status", JSONObject.toJSONString(msgDTO));
    }



}
