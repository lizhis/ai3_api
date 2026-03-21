package com.ai.basecommon.enums;

import java.io.Serializable;

public enum ServiceChatUserStatusEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    OFFLINE(0, "不在线"),
    ONLINE(1, "在线"),
    ENTRE(2, "已进入"),
    TYPING(3, "输入中"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    ServiceChatUserStatusEnum(Integer code, String value) {
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
