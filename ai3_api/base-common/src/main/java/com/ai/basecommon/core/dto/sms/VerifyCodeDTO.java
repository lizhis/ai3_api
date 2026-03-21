package com.ai.basecommon.core.dto.sms;

import com.ai.basecommon.enums.SmsYzmTypeEnum;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class VerifyCodeDTO {

    private String tel;
    private Integer code;
    private SmsYzmTypeEnum typeEnum;

}
