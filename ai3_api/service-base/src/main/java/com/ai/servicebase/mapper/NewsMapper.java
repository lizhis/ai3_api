package com.ai.servicebase.mapper;

import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.vo.base.NewsDetailVO;
import com.ai.basecommon.core.vo.base.NewsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NewsMapper {

    //查询
    List<NewsVO> select(PageIn param);

    //查询详情
    NewsDetailVO findById(@Param("id") Long id);




}
