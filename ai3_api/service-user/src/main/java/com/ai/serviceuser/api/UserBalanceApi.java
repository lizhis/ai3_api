package com.ai.serviceuser.api;

import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.po.user.UserBalancePO;
import com.ai.serviceuser.mapper.UserBalanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/userBalanceApi",produces = "application/json;charset=utf-8")
public class UserBalanceApi {

    @Autowired
    private UserBalanceMapper userBalanceMapper;



    @RequestMapping("/findByUserId")
    public UserBalancePO findByUserId(@RequestBody Long userId) throws Exception{
        if(null == userId){
            return null;
        }
        return userBalanceMapper.findByUserId(userId);
    }

    @RequestMapping("/incAmount")
    public boolean incAmount(@RequestBody UserBalanceChangeDTO param) throws Exception{
        if(null == param || null == param.getUserId() || null == param.getAmount()){
            return false;
        }
        return userBalanceMapper.incAmount(param.getUserId(),param.getAmount());
    }

    @RequestMapping("/decAmount")
    public boolean decAmount(@RequestBody UserBalanceChangeDTO param) throws Exception{
        if(null == param || null == param.getUserId() || null == param.getAmount()){
            return false;
        }
        return userBalanceMapper.decAmount(param.getUserId(),param.getAmount());
    }




}
