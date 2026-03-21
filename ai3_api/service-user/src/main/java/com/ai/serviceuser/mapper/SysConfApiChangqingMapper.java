package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiChangqingPO;
import com.ai.basecommon.core.vo.base.PayChangqingVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiChangqingMapper {


    List<SysConfApiChangqingPO> select();

    List<PayChangqingVO> selectVOList();

    SysConfApiChangqingPO findById(@Param("id") Long id);

}
