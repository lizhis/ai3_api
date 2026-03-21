package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.param.user.WithdrawRecordParam;
import com.ai.basecommon.core.po.user.WithdrawRecordPO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WithdrawRecordMapper {


    List<WithdrawRecordPO> select(WithdrawRecordParam param);



}
