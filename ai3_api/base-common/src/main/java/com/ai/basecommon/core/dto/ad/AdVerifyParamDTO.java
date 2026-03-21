package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdVerifyParamDTO extends BaseDTO {

    private String deviceId;
    private String checkId;
    private String ip;
    private String appPackage;

}
