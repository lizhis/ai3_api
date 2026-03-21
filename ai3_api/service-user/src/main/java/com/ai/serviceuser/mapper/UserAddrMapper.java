package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.user.UserAddrVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserAddrMapper {

    int insert(UserAddrPO po);

    List<UserAddrVO> select(@Param("userId") Long userId);

    UserAddrPO findById(@Param("id") Long id);

    UserAddrVO findByMyAddr(@Param("userId") Long userId,@Param("id") Long id);

    UserAddrVO findDefaultAddr(@Param("userId") Long userId);

    int countForDefault(@Param("userId") Long userId);

    //关掉默认地址
    boolean isDefaultOff(@Param("userId") Long userId);

    int update(UserAddrPO po);

}
