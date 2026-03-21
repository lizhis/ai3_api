package com.ai.basewebsocket.task.config;


import com.ai.basewebsocket.mapper.UserMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public class ScheduleDestroy {

    @Autowired
    private UserMapper userMapper;

    @PreDestroy
    public void destroy() throws Exception {

        userMapper.updateAllOffline();

    }


}
