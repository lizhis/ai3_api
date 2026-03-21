package com.ai.servicebase.mapper;

import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.po.base.article.AnnounPO;
import com.ai.basecommon.core.vo.base.AnnounVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AnnounMapper {


    //查询
    List<AnnounVO> select(PageIn param);

    //查询详情
    AnnounVO findById(@Param("id") Long id);




}
