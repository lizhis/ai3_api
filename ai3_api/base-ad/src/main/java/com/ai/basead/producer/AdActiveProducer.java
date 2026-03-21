package com.ai.basead.producer;

import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdActiveProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(AdActiveMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("ad_active", JSONObject.toJSONString(msgDTO));
    }



}
