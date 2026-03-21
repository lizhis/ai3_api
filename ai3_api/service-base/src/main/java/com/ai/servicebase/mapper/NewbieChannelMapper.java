package com.ai.servicebase.mapper;

import com.ai.basecommon.core.vo.base.NewbieChannelVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NewbieChannelMapper {


    //查询
    List<NewbieChannelVO> select();



}
