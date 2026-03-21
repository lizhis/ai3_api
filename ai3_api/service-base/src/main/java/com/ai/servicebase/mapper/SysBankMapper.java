package com.ai.servicebase.mapper;

import com.ai.basecommon.core.vo.base.SysBankVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysBankMapper {

    List<SysBankVO> select();

}
