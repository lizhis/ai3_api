package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiHuadaPO;
import com.ai.basecommon.core.vo.base.PayHuadaVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiHuadaMapper {

    List<SysConfApiHuadaPO> select();

    List<PayHuadaVO> selectVOList();

    SysConfApiHuadaPO findById(@Param("id") Long id);

}
