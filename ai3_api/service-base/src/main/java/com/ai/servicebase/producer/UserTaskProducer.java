package com.ai.servicebase.producer;

import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTaskProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(UserTaskMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_task", JSONObject.toJSONString(msgDTO));
    }



}
