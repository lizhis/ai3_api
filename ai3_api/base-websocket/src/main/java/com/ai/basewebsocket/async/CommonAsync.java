package com.ai.basewebsocket.async;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ws.WebSocketDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.enums.WsCodeEnum;
import com.ai.basewebsocket.common.RedisUtilX;
import com.ai.basewebsocket.connect.WebSocketServer;
import com.ai.basewebsocket.mapper.ServiceChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CommonAsync {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private ServiceChatMapper serviceChatMapper;


    @Async
    public void wsNotifyServiceUnRead(String deviceId,int millis) {
        if(millis < 100){
            millis = 100;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String openChatKey = RedisKey.device_id_to_chat_ + deviceId;
        if(!redisUtilX.hasKey(openChatKey)){
            return;
        }
        Long chatId = null;
        try{
            chatId = Long.parseLong(redisUtilX.get(openChatKey));
        }catch (Exception e){}

        if(null == chatId){
            return;
        }
        Integer num = serviceChatMapper.findUserUnread(chatId);
        if(null == num){
            num = 0;
        }
        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setCode(WsCodeEnum.SERVICE_CHAT_UNREAD.getCode());
        webSocketDTO.setContent(num);
        WebSocketServer.sendDeviceId(deviceId, webSocketDTO);
    }


}
