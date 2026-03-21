package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.param.pro.MyProListParam;
import com.ai.basecommon.core.po.pro.ProOrderPO;
import com.ai.basecommon.core.vo.pro.MyProVO;
import com.ai.basecommon.core.vo.pro.MyProDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProOrderMapper {

    int insertGetId(ProOrderPO po);

    ProOrderPO findByOrderId(@Param("orderId") String orderId);

    List<MyProVO> myList(MyProListParam param);

    //我的详情
    MyProDetailVO myDetail(@Param("userId") Long userId, @Param("orderId") String orderId);


    //查询用户购买次数
    int countByUserIdAndProId(@Param("userId") Long userId,@Param("proId") Long proId);


    //查询用户当天购买次数
    int countByYmdAndUserIdAndCarId(@Param("ymd") Integer ymd,@Param("userId") Long userId,@Param("proId") Long proId);





}
