package com.ai.serviceuser.handler;

import com.ai.basecommon.core.param.user.WithdrawRecordParam;
import com.ai.basecommon.core.po.user.WithdrawRecordPO;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.WithdrawRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WithdrawRecordHandler {

    @Autowired
    private WithdrawRecordMapper withdrawRecordMapper;

    @Autowired
    private UserUtilX userUtilX;



    //提现记录
    @ReadOnly
    public List<WithdrawRecordPO> select(WithdrawRecordParam param) throws Exception{
        if(null == param){
            param = new WithdrawRecordParam();
        }
        param.setUserId(userUtilX.getUserId());
        Long time = System.currentTimeMillis() - 86400000L * 15;
        param.setTime(time);
        return withdrawRecordMapper.select(param);
    }




}
