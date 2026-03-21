package com.ai.basecommon.core.dto.sms;

import com.ai.basecommon.enums.SmsYzmTypeEnum;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class SmsSendDTO {

    private String tel;
    private String deviceId;
    private SmsYzmTypeEnum typeEnum;

}
