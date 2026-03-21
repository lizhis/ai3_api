package com.ai.basecommon.enums;

import java.io.Serializable;

public enum BlessingTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    CARD1(1, "注册"),
    CARD2(2, "实名"),
    CARD3(3, "签到"),
    CARD4(4, "签到3天"),
    CARD5(5, "签到7天"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    BlessingTypeEnum(Integer code, String value) {
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
