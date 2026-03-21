package com.ai.basecommon.core.po.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopPO {

    private Long id;
    private String name;
    private String image;
    private String content;
    private Integer price;
    private BigDecimal amount;
    private Long cateId;
    private Integer isVirtual;
    private Integer sort;
    private Integer status;
    private Integer isDel;
    private Long createTime;
    private Long updateTime;

}
