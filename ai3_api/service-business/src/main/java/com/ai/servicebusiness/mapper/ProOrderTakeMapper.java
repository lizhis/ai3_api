package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.vo.pro.MyProOrderTakeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProOrderTakeMapper {

    //查询我的接单列表
    List<MyProOrderTakeVO> myTakeList(@Param("orderId") String orderId);

}
