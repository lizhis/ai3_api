package com.ai.basewebsocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ServiceChatMapper {


    Integer findUserUnread(@Param("id") Long id);



}
