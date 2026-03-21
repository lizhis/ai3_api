package com.ai.serviceuser.api;

import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.dto.user.UserIntegralChangeDTO;
import com.ai.serviceuser.mapper.UserBalanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;

@RestController
@RequestMapping(value = "/userIntegralApi",produces = "application/json;charset=utf-8")
public class UserIntegralApi {

    @Autowired
    private UserBalanceMapper userBalanceMapper;



    @RequestMapping("/incIntegral")
    public boolean incIntegral(@RequestBody UserBalanceChangeDTO param) throws Exception{
        if(null == param || null == param.getUserId() || null == param.getAmount()){
            return false;
        }
        Integer num = param.getAmount().setScale(0, RoundingMode.DOWN).intValue();
        return userBalanceMapper.incIntegral(param.getUserId(),num);
    }



}
