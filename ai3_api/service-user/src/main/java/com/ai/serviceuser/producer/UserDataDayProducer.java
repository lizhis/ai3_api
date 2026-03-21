package com.ai.serviceuser.producer;

import com.ai.basecommon.core.dto.msg.UserDataDayMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataDayProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(UserDataDayMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_data_day", JSONObject.toJSONString(msgDTO));
    }



}
