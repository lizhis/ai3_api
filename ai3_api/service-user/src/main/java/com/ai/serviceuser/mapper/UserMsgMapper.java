package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.po.user.UserMsgPO;
import com.ai.basecommon.core.vo.user.UserMsgVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMsgMapper {

    List<UserMsgVO> select(PageIn param);

    UserMsgPO findById(@Param("id") Long id);

    UserMsgVO findMyNewMsg(@Param("userId") Long userId);

    int countUnRead(@Param("userId") Long userId);

    boolean updateRead(@Param("id") Long id,@Param("time") Long time);

    boolean updateNotice(@Param("id") Long id);

}
