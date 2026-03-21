package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.dto.user.UserEnergyChangeDTO;
import com.ai.basecommon.core.dto.user.UserIntegralChangeDTO;
import com.ai.basecommon.core.po.user.UserBalancePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface UserBalanceMapper {


    int insert(UserBalancePO po);

    UserBalancePO findByUserId(@Param("userId") Long userId);

    //查询用户余额
    BigDecimal findAmountByUserId(@Param("userId") Long userId);

    //查询用户全部余额
    BigDecimal findAllAmountByUserId(@Param("userId") Long userId);


    //查询用户云币值
    Integer findEnergyByUserId(@Param("userId") Long userId);


    //查询用户积分
    Integer findIntegralByUserId(@Param("userId") Long userId);

    //查询用户云豆
    Integer findGoldByUserId(@Param("userId") Long userId);


    boolean incAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    boolean decAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    boolean incEnergy(@Param("userId") Long userId,@Param("num") Integer num);

    boolean decEnergy(@Param("userId") Long userId,@Param("num") Integer num);

    boolean incIntegral(@Param("userId") Long userId,@Param("num") Integer num);

    boolean decIntegral(@Param("userId") Long userId,@Param("num") Integer num);



    boolean incFreezeAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);
    boolean decFreezeAmount(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    boolean goldExchange(@Param("userId") Long userId,@Param("amount") BigDecimal amount,@Param("gold") Integer gold);


    boolean incGold(@Param("userId") Long userId,@Param("gold") Integer gold);



}
