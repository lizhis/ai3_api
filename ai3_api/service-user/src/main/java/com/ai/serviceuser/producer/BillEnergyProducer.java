package com.ai.serviceuser.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.BillEnergyMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillEnergyProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(BillEnergyMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("bill_energy", JSONObject.toJSONString(msgDTO));
    }



}
