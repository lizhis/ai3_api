package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.param.shop.MyOrderParam;
import com.ai.basecommon.core.po.shop.ShopOrderPO;
import com.ai.basecommon.core.vo.shop.ShopOrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ShopOrderMapper {


    int insertGetId(ShopOrderPO po);


    ShopOrderPO findByOrderId(@Param("orderId") String orderId);

    List<ShopOrderVO> myOrder(MyOrderParam param);

    //确认收货
    boolean confirm(@Param("orderId") String orderId,@Param("time") Long time);


}
