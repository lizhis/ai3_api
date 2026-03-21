package com.ai.serviceuser.mapper;


import com.ai.basecommon.core.po.user.UserPassPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public interface UserPassMapper {


    int insert(UserPassPO po);

    UserPassPO findByUserId(@Param("userId") Long userId);

    boolean updatePass(@Param("userId") Long userId,@Param("password") String password);

    boolean updatePassPay(@Param("userId") Long userId,@Param("passwordPay") String passwordPay);

}
