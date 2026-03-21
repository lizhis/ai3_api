package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MyProDetailVO {


    private String title;
    private String image;
    private BigDecimal payAmount;
    private BigDecimal totalTakeAmount;
    private BigDecimal totalFee;
    private BigDecimal totalEarning;
    private Integer putDays;
    private Integer cateType;
    private Integer checkType;
    private Integer isAutoNext;
    private Integer status;
    private Long createTime;
    private Long startTime;
    private Long endTime;

    private List<MyProOrderTakeVO> takeList;

}
