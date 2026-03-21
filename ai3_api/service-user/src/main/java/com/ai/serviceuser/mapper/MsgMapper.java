package com.ai.serviceuser.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface MsgMapper {


    boolean incReadNum(@Param("id") Long id);


}
