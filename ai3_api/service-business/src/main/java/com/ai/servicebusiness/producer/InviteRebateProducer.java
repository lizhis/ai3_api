package com.ai.servicebusiness.producer;

import com.ai.basecommon.core.dto.msg.InviteRebateMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InviteRebateProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(InviteRebateMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("invite_rebate", JSONObject.toJSONString(msgDTO));
    }



}
