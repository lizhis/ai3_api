package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiHuada2PO;
import com.ai.basecommon.core.vo.base.PayHuada2VO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiHuada2Mapper {

    List<SysConfApiHuada2PO> select();

    List<PayHuada2VO> selectVOList();

    SysConfApiHuada2PO findById(@Param("id") Long id);

}
