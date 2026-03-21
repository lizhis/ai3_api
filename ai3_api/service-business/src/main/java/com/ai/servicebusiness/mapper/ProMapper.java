package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.param.pro.ProParam;
import com.ai.basecommon.core.po.pro.ProPO;
import com.ai.basecommon.core.vo.pro.ProVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProMapper {


    List<ProVO> select(ProParam param);

    List<ProVO> selectHome(@Param("level") Integer level);

    List<ProVO> selectByIds(List<Long> ids);

    List<String> selectTitleByIds(List<Long> ids);

    ProVO findVOById(@Param("id") Long id);

    ProPO findById(@Param("id") Long id);


    boolean compNumDec(@Param("id") Long id,@Param("num") Integer num);



}
