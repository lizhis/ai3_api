package com.ai.serviceuser.producer;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.utils.LogUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WsProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(WsSendDTO msgDTO) {
        if(null == msgDTO){
            return;
        }
        //LogUtil.log("发布ws消息：" + msgDTO);
        rabbitTemplate.convertAndSend("ws", JSONObject.toJSONString(msgDTO));
    }



}
