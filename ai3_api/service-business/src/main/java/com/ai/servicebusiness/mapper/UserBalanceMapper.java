package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.user.UserBalancePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface UserBalanceMapper {


    UserBalancePO findByUserId(@Param("userId") Long userId);


    boolean incAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    boolean decAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    boolean incEnergy(@Param("userId") Long userId,@Param("num") Integer num);

    boolean decEnergy(@Param("userId") Long userId,@Param("num") Integer num);

    boolean incIntegral(@Param("userId") Long userId,@Param("num") Integer num);

    boolean decIntegral(@Param("userId") Long userId,@Param("num") Integer num);


}
