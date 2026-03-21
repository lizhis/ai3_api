package com.ai.serviceuser.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAssetTrendsProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(UserAssetTrendsMsgDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        rabbitTemplate.convertAndSend("user_asset_trends", JSONObject.toJSONString(msgDTO));
    }



}
