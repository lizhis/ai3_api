package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiQilinPO;
import com.ai.basecommon.core.vo.base.PayQilinVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiQilinMapper {

    List<SysConfApiQilinPO> select();

    List<PayQilinVO> selectVOList();

    SysConfApiQilinPO findById(@Param("id") Long id);

}
