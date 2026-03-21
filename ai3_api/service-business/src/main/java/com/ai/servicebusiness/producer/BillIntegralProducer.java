package com.ai.servicebusiness.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.BillIntegralMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillIntegralProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(BillIntegralMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("bill_integral", JSONObject.toJSONString(msgDTO));
    }



}
