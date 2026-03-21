package com.ai.basecommon.core.vo.user;

import com.ai.basecommon.core.po.user.BlessingShopOrderPO;
import lombok.Data;

import java.util.List;

@Data
public class BlessingShopOrderDetailVO extends BlessingShopOrderPO {


    //快递动态
    private List<DeliveryDataItem> deliveryDataItems;

    @Data
    public static class DeliveryDataItem{
        private String time;
        private String context;
    }

}
