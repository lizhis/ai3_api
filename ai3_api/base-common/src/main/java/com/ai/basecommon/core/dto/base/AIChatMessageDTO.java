package com.ai.basecommon.core.dto.base;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AIChatMessageDTO extends BaseDTO {

    private String msgId;
    private String role;
    private String content;
    private Long time;

}
