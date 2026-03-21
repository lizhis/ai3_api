package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserBlessingPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserBlessingMapper {


    //卡片数量
    int countByUserIdPassInvite(@Param("userId") Long userId);

    //邀请卡数量
    int countByUserIdInviteCard(@Param("userId") Long userId);

    //查询邀请卡ID列表
    List<Long> selectIdsInviteCard(@Param("userId") Long userId);


    int existCard(@Param("userId") Long userId,@Param("blessingType") Integer blessingType);

    List<Integer> selectTypeByUserIdPassInvite(@Param("userId") Long userId);

    int insert(UserBlessingPO po);


    boolean decInviteCard(List<Long> list);




}
