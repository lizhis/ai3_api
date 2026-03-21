package com.ai.serviceuser.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GiveCarProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(Long userId) {
        if(null == userId || userId < 1){
            return;
        }
        rabbitTemplate.convertAndSend("give_car", userId.toString());
    }



}
