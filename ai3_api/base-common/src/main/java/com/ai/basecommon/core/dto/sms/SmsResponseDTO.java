package com.ai.basecommon.core.dto.sms;

import lombok.Data;

@Data
public class SmsResponseDTO {

    //手机号
    private String tel;

    //参数
    private String param;

    //模板内容
    private String content;

    //错误信息
    private String errorMsg;

    //发送状态
    private Integer sendStatus = 1;

}
