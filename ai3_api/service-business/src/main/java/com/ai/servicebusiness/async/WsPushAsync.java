package com.ai.servicebusiness.async;

import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.enums.WsCodeEnum;
import com.ai.servicebusiness.producer.WsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WsPushAsync {


    @Autowired
    private WsProducer wsProducer;


    @Async
    public void pushBalance(Long userId,int millis) {
        if(null == userId){
            return;
        }
        if(millis < 100){
            millis = 100;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WsSendDTO wsSendDTO2 = new WsSendDTO();
        wsSendDTO2.setUserId(userId);
        wsSendDTO2.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO2);
    }




}
