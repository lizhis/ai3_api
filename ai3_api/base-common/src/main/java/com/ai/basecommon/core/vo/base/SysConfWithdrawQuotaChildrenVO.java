package com.ai.basecommon.core.vo.base;

import lombok.Data;

@Data
public class SysConfWithdrawQuotaChildrenVO {
    private Long id; //提现券ID
    private Integer amount; //提现券金额
    private Integer status; //状态 1为可用  3为未激活
    private Integer needDays; //激活所需签到的天数  只会在status为3的情况下会出现
    private Long createTime;
}
