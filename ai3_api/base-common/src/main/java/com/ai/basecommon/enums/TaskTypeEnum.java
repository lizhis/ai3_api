package com.ai.basecommon.enums;

import java.io.Serializable;

public enum TaskTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    SIGN_IN(1, "签到"),
    AUTH(2, "实名"),
    INVITE(3, "邀请"),
    LEASE(4, "租赁"),

    RECHARGE(10, "充值"),
    WITHDRAW(15, "提现"),
    WITHDRAW_BANK(16, "银行提现"),

    STEP_GOLD(20, "步步生金"),
    STEP_GOLD_CLOCK(21, "早晚打卡"),
    STEP_GOLD_WATER(22, "喝水"),

    AI_CHAT(25, "AI问答"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    TaskTypeEnum(Integer code, String value) {
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


    public static TaskTypeEnum getByCode(int code) {
        TaskTypeEnum[] values = TaskTypeEnum.values();

        for (TaskTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
