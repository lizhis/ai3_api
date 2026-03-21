package com.ai.basecommon.enums;

import java.io.Serializable;

public enum EarningTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    EACH_DAY_UNTIL(1, "每天返息，到期还本"),
    EACH_WEEK_UNTIL(2, "每周返息，到期还本"),
    EACH_MONTH_UNTIL(3, "每月返息，到期还本"),
    EACH_DAY_ALWAYS(4, "每天复利，到期还本"),
    UNTIL(5, "到期还本付息"),





    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    EarningTypeEnum(Integer code, String value) {
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
