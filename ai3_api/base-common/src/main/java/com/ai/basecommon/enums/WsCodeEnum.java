package com.ai.basecommon.enums;

import java.io.Serializable;

public enum WsCodeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    USER_INFO(1, "用户信息"),
    USER_BALANCE(2, "用户资产"),
    USER_FREEZE(4, "用户冻结"),
    USER_NEWBIE_STATUS(5, "用户新手任务状态"),
    USER_NEW_MSG(6, "最新消息变化"),
    USER_TASK(7, "用户任务"),

    AI_CHAT(9, "ai问答"),
    SYS_CONF(10, "系统配置"),

    REJECT(21, "封禁"),



    SERVICE_CHAT_UNREAD(31, "客服消息未读数量"),
    SERVICE_CHAT_ADD(32, "客服消息增加"),
    SERVICE_CHAT_DELETE(33, "客服消息删除"),




    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    WsCodeEnum(Integer code, String value) {
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


    public static WsCodeEnum getByCode(int code) {
        WsCodeEnum[] values = WsCodeEnum.values();

        for (WsCodeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
