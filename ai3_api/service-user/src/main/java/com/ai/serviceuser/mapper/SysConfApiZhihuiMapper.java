package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiZhihuiPO;
import com.ai.basecommon.core.vo.base.PayZhihuiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiZhihuiMapper {

    List<SysConfApiZhihuiPO> select();

    List<PayZhihuiVO> selectVOList();

    SysConfApiZhihuiPO findById(@Param("id") Long id);

}
