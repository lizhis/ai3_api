package com.ai.servicebusiness.mapper;


import com.ai.basecommon.core.po.user.UserPassPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public interface UserPassMapper {

    UserPassPO findByUserId(@Param("userId") Long userId);

}
