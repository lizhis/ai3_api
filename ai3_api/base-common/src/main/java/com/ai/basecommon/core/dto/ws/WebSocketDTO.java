package com.ai.basecommon.core.dto.ws;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 *
 */
@Data
public class WebSocketDTO<T> extends BaseDTO {

    private Integer code;//自定义消息业务类型

    private T content;//消息内容

    private T mark;//额外字段

}
