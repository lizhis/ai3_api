package com.ai.basecommon.core.po.base;

import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AiChatRecordPO {

    private Long id;
    private String deviceId;
    private Long userId;
    private String content;
    private String reply;
    private String apiMsgId;
    private Integer ymd;
    private Integer status;
    private Integer isDel;
    private Long replyTime;
    private Long createTime;
    private Long updateTime;



}
