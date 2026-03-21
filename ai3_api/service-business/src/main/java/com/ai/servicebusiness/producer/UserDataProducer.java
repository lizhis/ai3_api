package com.ai.servicebusiness.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.UserDataMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(UserDataMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_data", JSONObject.toJSONString(msgDTO));
    }



}
