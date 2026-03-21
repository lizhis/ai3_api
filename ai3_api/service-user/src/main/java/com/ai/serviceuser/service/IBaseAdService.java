package com.ai.serviceuser.service;


import com.ai.basecommon.core.dto.ad.AdVerifyParamDTO;
import com.ai.serviceuser.service.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "base-ad",configuration = FeignConfig.class)
public interface IBaseAdService {

    @PostMapping("/adApi/active")
    void active(String id) throws Exception;

    @PostMapping("/adApi/oceanengineActive")
    void oceanengineActive(AdVerifyParamDTO paramDTO) throws Exception;

    @PostMapping("/adApi/verify")
    Integer verify(AdVerifyParamDTO paramDTO) throws Exception;

    @PostMapping("/adApi/remain")
    void remain(String deviceId) throws Exception;



}
