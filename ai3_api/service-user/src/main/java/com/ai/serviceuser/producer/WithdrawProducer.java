package com.ai.serviceuser.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.WithdrawMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(WithdrawMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_withdraw", JSONObject.toJSONString(msgDTO));
    }



}
