package com.ai.basecommon.core.vo.user;

import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class TaskVO {

    private Integer days;
    private Integer number;
    private String title;
    private String content;
    private Integer giveType;
    private String giveContent;
    private String taskDescImg;
    private String taskDesc;
    private Integer progressAll;
    private Integer progress;
    private Integer status;


}
