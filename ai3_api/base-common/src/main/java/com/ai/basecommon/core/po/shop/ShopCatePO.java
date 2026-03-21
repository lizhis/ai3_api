package com.ai.basecommon.core.po.shop;

import lombok.Data;

@Data
public class ShopCatePO {

    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
    private Long createTime;
    private Long updateTime;

}
