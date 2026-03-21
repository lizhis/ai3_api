package com.ai.basecommon.core.vo.user;

import com.ai.basecommon.core.vo.base.SysPopVO;
import com.ai.basecommon.core.vo.step.MyClockVO;
import lombok.Data;

@Data
public class UserInitDataVO {

    private UserInfoVO userInfoVO;
    private UserBalanceVO userBalanceVO;
    private MyClockVO myClockVO;

    private SysPopVO sysPopVO;


}
