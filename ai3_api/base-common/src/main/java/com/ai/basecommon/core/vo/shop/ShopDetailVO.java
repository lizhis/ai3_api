package com.ai.basecommon.core.vo.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopDetailVO {

    private Long id;
    private String name;
    private String image;
    private String content;
    private Integer price;
    private BigDecimal amount;
    private Integer isVirtual;

}
