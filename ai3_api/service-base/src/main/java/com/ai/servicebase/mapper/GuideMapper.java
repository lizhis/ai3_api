package com.ai.servicebase.mapper;

import com.ai.basecommon.core.param.base.GuideParam;
import com.ai.basecommon.core.po.base.article.GuidePO;
import com.ai.basecommon.core.vo.base.GuideVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GuideMapper {


    //查询
    List<GuideVO> select(GuideParam param);

    //查询详情
    GuideVO findVOById(@Param("id") Long id);




}
