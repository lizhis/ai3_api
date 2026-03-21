package com.ai.basecommon.core.po.shop;

import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class GiftCodeRecordPO {

    private Long id;
    private String code;
    private String orderId;
    private String shopName;
    private Long userId;
    private String realName;
    private Integer ymd;
    private Long createTime;
    private Long updateTime;
}
