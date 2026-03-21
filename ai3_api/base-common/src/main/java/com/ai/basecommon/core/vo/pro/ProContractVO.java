package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class ProContractVO {

    private Long id;
    private String orderId;
    private String realName;
    private String title;
    private Long startTime;
    private Long endTime;
    private Long createTime;
    private String bbName;
    private String bbImage;
    private Integer checkType;
    private Integer cateType;
    private String idCard;
    private BigDecimal payAmount;
    private BigDecimal incomeFloor;
    private Integer isAutoNext;
}
