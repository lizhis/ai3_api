package com.ai.servicebase.mapper;

import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.vo.base.AboutusVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AboutusMapper {


    //查询
    List<AboutusVO> select(PageIn param);

    //查询详情
    AboutusVO findById(@Param("id") Long id);




}
