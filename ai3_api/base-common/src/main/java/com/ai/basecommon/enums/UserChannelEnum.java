package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserChannelEnum implements BaseEnumInterface<Integer, String>, Serializable {


    NO_CHANNEL(1, "无渠道"),

    UC(2, "超级汇川"),

    TENCENT(3, "腾讯广告"),

    TENCENT2(31, "腾讯广告2"),

    TENCENT3(33, "腾讯广告3"),
    TENCENT4(34, "腾讯广告4"),

    OCEANENGINE(4, "巨量引擎"),

    BAIDU(5, "百度广告"),

    KWAI(6, "快手广告"),

    BIANXIANMAO(7, "变现猫"),

    XINGTU(8, "巨量星图"),






    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    UserChannelEnum(Integer code, String value) {
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


    public static UserChannelEnum getByCode(int code) {
        UserChannelEnum[] values = UserChannelEnum.values();

        for (UserChannelEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
