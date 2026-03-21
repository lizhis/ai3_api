package com.ai.basewebsocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    //查询所有在线的用户id
    List<Long> selectAllOnlineIds();

    //上下线
    boolean updateOnline(@Param("userId") Long userId);
    boolean updateOffline(@Param("userId") Long userId);


    //全员下线
    boolean updateAllOffline();

}
