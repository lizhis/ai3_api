package com.ai.basecommon.core.vo.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopVO {
    private Long id;
    private Long cateId;
    private String name;
    private String image;
    private Integer price;
    private BigDecimal amount;
    private Integer isVirtual;
    private Integer sort;
}
