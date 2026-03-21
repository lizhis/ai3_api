package com.ai.servicebase.producer;

import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLogProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(UserLogMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_log", JSONObject.toJSONString(msgDTO));
    }



}
