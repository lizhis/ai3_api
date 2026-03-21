package com.ai.servicebase.mapper;

import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.vo.base.FocusVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FocusMapper {


    //查询
    List<FocusVO> select(PageIn param);

    //查询详情
    FocusVO findById(@Param("id") Long id);




}
