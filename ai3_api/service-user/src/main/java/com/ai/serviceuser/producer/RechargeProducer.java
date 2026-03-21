package com.ai.serviceuser.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.RechargeMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RechargeProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(RechargeMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_recharge", JSONObject.toJSONString(msgDTO));
    }



}
