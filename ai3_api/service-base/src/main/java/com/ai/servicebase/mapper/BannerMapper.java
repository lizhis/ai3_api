package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.BannerPO;
import com.ai.basecommon.core.vo.base.BannerVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BannerMapper {


    //查询
    List<BannerVO> select();



}
