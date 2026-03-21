package com.ai.basecommon.core.vo.shop;

import com.ai.basecommon.core.po.shop.ShopOrderPO;
import lombok.Data;

import java.util.List;

@Data
public class ShopOrderDetailVO extends ShopOrderPO {


    //快递动态
    private List<DeliveryDataItem> deliveryDataItems;

    @Data
    public static class DeliveryDataItem{
        private String time;
        private String context;
    }

}
