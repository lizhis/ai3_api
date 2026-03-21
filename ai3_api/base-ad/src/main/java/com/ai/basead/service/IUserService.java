package com.ai.basead.service;

import com.ai.basead.service.feign.FeignConfig;
import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.dto.user.UserEnergyChangeDTO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.po.user.UserBalancePO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-user",configuration = FeignConfig.class)
public interface IUserService {


    @PostMapping("/userBalanceApi/findByUserId")
    UserBalancePO findBalancePO(Long userId) throws Exception;

    @PostMapping("/userBalanceApi/decAmount")
    boolean decAmount(UserBalanceChangeDTO param) throws Exception;



    @PostMapping("/userEnergyApi/findEnergy")
    Integer findEnergy(Long userId) throws Exception;

    @PostMapping("/userEnergyApi/energyInc")
    boolean energyInc(UserEnergyChangeDTO param) throws Exception;

    @PostMapping("/userEnergyApi/energyDec")
    boolean energyDec(UserEnergyChangeDTO param) throws Exception;


    //地址
    @PostMapping("/userEnergyApi/findAddrById")
    UserAddrPO findAddrById(Long addrId) throws Exception;


    //查询第三方配置
    @PostMapping("/baseApi/findSysConfApi")
    SysConfApiPO findSysConfApi() throws Exception;



}
