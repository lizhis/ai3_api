package com.ai.serviceuser.consumer;

import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.RedisUtilX;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//,concurrency = "5-10"
@Component
@RabbitListener(queues = "ad_active",containerFactory = "adActiveContainerFactory")
public class AdActiveConsumer {

    @Autowired
    private RedisUtilX redisUtilX;

    @RabbitHandler
    public void receive(String msg) {
        if(StringUtil.isEmpty(msg)){
            return;
        }
        AdActiveMsgDTO msgDTO = null;
        try{
            msgDTO = JSONObject.parseObject(msg,AdActiveMsgDTO.class);
        }catch (Exception e){
            LogUtil.log("mq消费消息失败：" + e.getMessage());
            return;
        }
        if(null == msgDTO || StringUtil.isEmpty(msgDTO.getActiveId()) || null == msgDTO.getChannel()){
            return;
        }
        //LogUtil.log("消息队列 收到激活标记：" + msg);
        String k = "ad_active_id_" + msgDTO.getActiveId();
        redisUtilX.set(k,msgDTO.getChannel().toString(),3600*48);

    }

}
