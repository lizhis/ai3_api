package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiCaiyuanPO;
import com.ai.basecommon.core.vo.base.PayCaiyuanVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfApiCaiyuanMapper {


    List<SysConfApiCaiyuanPO> select();

    List<PayCaiyuanVO> selectVOList();

    SysConfApiCaiyuanPO findById(@Param("id") Long id);

}
