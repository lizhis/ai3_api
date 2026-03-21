package com.ai.basewebsocket.task;

import com.ai.basewebsocket.mapper.UserMapper;
import com.ai.basewebsocket.connect.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author
 */
@Component("OnlineCheckTask")
public class OnlineCheckTask {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    public void doTask() {

        //查出所有数据库里在线的人
        List<Long> userIds = userMapper.selectAllOnlineIds();
        if(null == userIds || userIds.isEmpty()){
            return;
        }

        Map<Long, List<String>> channelUsers = webSocketServer.getChannelUsers();
        if(null == channelUsers){
            channelUsers = new ConcurrentHashMap<>();
        }

        Set<Long> onlineIds = channelUsers.keySet();

        //核对是否在线  如果不在线则把数据库状态改为下线
        for(Long userId : userIds){
            if(!onlineIds.contains(userId)){
                userMapper.updateOffline(userId);
            }
        }






    }




}