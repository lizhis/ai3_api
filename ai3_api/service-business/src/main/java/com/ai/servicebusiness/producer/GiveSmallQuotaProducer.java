package com.ai.servicebusiness.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GiveSmallQuotaProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(Long userId) {
        if(null == userId || userId < 1){
            return;
        }
        rabbitTemplate.convertAndSend("give_small_quota", userId.toString());
    }



}
