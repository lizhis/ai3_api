package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserDataPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface UserDataMapper {

    int insert(UserDataPO po);

    int countByUserId(@Param("userId") Long userId);

    UserDataPO findByUserId(@Param("userId") Long userId);

    int findSignNumByUserId(@Param("userId") Long userId);

    boolean deleteByUserId(@Param("userId") Long userId);


    //查询总租赁金额
    BigDecimal findLeaseSum(@Param("userId") Long userId);

    //查询租赁次数
    Integer findLeaseNum(@Param("userId") Long userId);


    //查询一级佣金
    BigDecimal findChildren1Sum(@Param("userId") Long userId);

    //查询福卡奖励
    BigDecimal findBlessingAmount(@Param("userId") Long userId);

    //更新福卡奖励
    boolean updateBlessingAmount(@Param("userId") Long userId,@Param("blessingAmount") BigDecimal blessingAmount);


    //1级下线人数
    boolean incChildren1Num(@Param("userId") Long userId);

    //2级下线人数
    boolean incChildren2Num(@Param("userId") Long userId);

    //3级下线人数
    boolean incChildren3Num(@Param("userId") Long userId);




    //1级下线返佣
    boolean incChildren1Sum(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    //2级下线返佣
    boolean incChildren2Sum(@Param("userId") Long userId,@Param("amount") BigDecimal amount);

    //3级下线返佣
    boolean incChildren3Sum(@Param("userId") Long userId,@Param("amount") BigDecimal amount);



}
