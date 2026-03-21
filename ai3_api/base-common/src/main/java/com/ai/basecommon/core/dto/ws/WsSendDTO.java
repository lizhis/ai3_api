package com.ai.basecommon.core.dto.ws;

import lombok.Data;

/**
 * @Description
 * @Author
 *
 */
@Data
public class WsSendDTO<T> {

    private Long userId;//用户ID

    private String deviceId;//用户设备

    private Integer code;//业务类型

    private T content;//消息内容

    private T mark;

}
