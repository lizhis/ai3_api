package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.pro.ProSkuPO;
import com.ai.basecommon.core.vo.pro.ProSkuVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProSkuMapper {

    ProSkuPO findById(@Param("id") Long id);

    List<ProSkuVO> selectVOByProId(@Param("proId") Long proId);




}
