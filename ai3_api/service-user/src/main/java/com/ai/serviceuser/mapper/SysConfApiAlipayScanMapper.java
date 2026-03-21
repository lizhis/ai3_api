package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiAlipayScanPO;
import com.ai.basecommon.core.vo.base.PayAlipayVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiAlipayScanMapper {


    List<SysConfApiAlipayScanPO> select();

    List<PayAlipayVO> selectVOList();

    SysConfApiAlipayScanPO findById(@Param("id") Long id);

}
