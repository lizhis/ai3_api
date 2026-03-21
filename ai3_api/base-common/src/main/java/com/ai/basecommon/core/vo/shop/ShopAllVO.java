package com.ai.basecommon.core.vo.shop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopAllVO {
    private List<ShopCateVO> cateList;
    private List<ShopVO> shopList;
}
