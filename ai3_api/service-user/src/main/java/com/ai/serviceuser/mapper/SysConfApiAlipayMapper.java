package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiAlipayPO;
import com.ai.basecommon.core.vo.base.PayAlipayVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiAlipayMapper {


    List<SysConfApiAlipayPO> select();

    List<PayAlipayVO> selectVOList();

    SysConfApiAlipayPO findById(@Param("id") Long id);

}
