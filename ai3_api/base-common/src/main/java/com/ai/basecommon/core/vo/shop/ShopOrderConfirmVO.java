package com.ai.basecommon.core.vo.shop;

import com.ai.basecommon.core.vo.user.UserAddrVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopOrderConfirmVO {

    private ShopVO shop;

    private UserAddrVO userAddr;
}
