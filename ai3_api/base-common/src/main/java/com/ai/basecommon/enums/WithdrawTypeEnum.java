package com.ai.basecommon.enums;

import java.io.Serializable;

public enum WithdrawTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    BANK(1, "银行卡"),
    ALIPAY(2, "支付宝"),





    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    WithdrawTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.value;
    }



}
