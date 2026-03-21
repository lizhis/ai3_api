package com.ai.serviceuser.api;

import com.ai.basecommon.core.dto.user.UserEnergyChangeDTO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.user.UserAddrVO;
import com.ai.serviceuser.handler.UserAddrHandler;
import com.ai.serviceuser.mapper.UserBalanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/userEnergyApi",produces = "application/json;charset=utf-8")
public class UserEnergyApi {

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserAddrHandler userAddrHandler;


    @RequestMapping("/findEnergy")
    public Integer findEnergy(@RequestBody Long userId) throws Exception{
        if(null == userId){
            return null;
        }
        return userBalanceMapper.findEnergyByUserId(userId);
    }

    @RequestMapping("/energyInc")
    public boolean energyInc(@RequestBody UserEnergyChangeDTO param) throws Exception{
        if(null == param || null == param.getUserId() || null == param.getNum()){
            return false;
        }
        return userBalanceMapper.incEnergy(param.getUserId(), param.getNum());
    }

    @RequestMapping("/energyDec")
    public boolean energyDec(@RequestBody UserEnergyChangeDTO param) throws Exception{
        if(null == param || null == param.getUserId() || null == param.getNum()){
            return false;
        }
        return userBalanceMapper.decEnergy(param.getUserId(), param.getNum());
    }

    @RequestMapping("/findAddrById")
    public UserAddrPO findAddrById(@RequestBody Long addrId) throws Exception{
        return userAddrHandler.findById(addrId);
    }

    @RequestMapping("/findDefaultAddr")
    public UserAddrVO findDefaultAddr(@RequestBody Long userId) throws Exception{
        return userAddrHandler.defaultAddrByApi(userId);
    }


}
