package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfWithdrawVO {
    private BigDecimal withdrawMin;
    private String withdrawDesc;
    private String alipayAccount;
    private String alipayRealName;
    private Integer quotaIsOpen; // 继续保留 用于是否显示小额提现的入口

    //这几个 即将废弃
    private BigDecimal quotaAmount;
    private String quotaDesc;
    private Integer quotaNum = 0;
    private Integer quotaWaitNum = 0;
    private Integer needSignDays = 0;

}
