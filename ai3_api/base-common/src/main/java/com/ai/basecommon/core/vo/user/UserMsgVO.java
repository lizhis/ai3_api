package com.ai.basecommon.core.vo.user;

import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class UserMsgVO {
    private Long id;
    private String title;
    private Integer isRead;
    private Long createTime;
}
