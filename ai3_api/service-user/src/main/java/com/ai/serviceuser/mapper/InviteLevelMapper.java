package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.InviteLevelPO;
import com.ai.basecommon.core.vo.user.MyChildrenUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InviteLevelMapper {


    int insert(InviteLevelPO po);

    InviteLevelPO findParent1(@Param("childUserId") Long childUserId);
    InviteLevelPO findParent2(@Param("childUserId") Long childUserId);
    InviteLevelPO findParent3(@Param("childUserId") Long childUserId);

    List<MyChildrenUserVO> selectChildren1List(@Param("userId") Long userId);


    boolean deleteByChildUserId(@Param("childUserId") Long childUserId);




}
