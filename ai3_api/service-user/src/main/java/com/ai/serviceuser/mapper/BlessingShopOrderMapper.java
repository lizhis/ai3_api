package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.BlessingShopOrderPO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlessingShopOrderMapper {


    int insertGetId(BlessingShopOrderPO po);

    BlessingShopOrderPO findByOrderId(@Param("orderId") String orderId);

    List<BlessingShopOrderVO> myOrder(@Param("userId") Long userId);

    //确认收货
    boolean confirm(@Param("orderId") String orderId,@Param("time") Long time);


}
