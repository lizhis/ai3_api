package com.ai.basecommon.core.param.user;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class UserMsgParam extends PageIn {

    @Schema(name = "userId",title = "userId",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

}
