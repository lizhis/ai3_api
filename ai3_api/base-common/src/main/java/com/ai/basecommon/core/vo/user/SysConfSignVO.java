package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author
 */
@Data
public class SysConfSignVO {

    private List<SysConfSignChildVO> along;
    private List<SysConfSignChildVO> total;

    private List<SignRecordVO> record;

}
