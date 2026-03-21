package com.ai.servicebase.mapper;

import com.ai.basecommon.core.vo.base.GuideCateVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GuideCateMapper {


    List<GuideCateVO> select();



}
