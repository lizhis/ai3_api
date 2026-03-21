package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.util.List;

@Data
public class SysConfWithdrawQuotaVO {

    private Integer quotaIsOpen;
    private String quotaDesc;

    private String alipayAccount;
    private String alipayRealName;

    private Integer isBoundBankcard; //是否绑定银行卡

    //小额提现券
    private List<SysConfWithdrawQuotaChildrenVO> list;

}
