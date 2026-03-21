package com.ai.servicebase.service;

import com.ai.servicebase.service.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-user",configuration = FeignConfig.class)
public interface IUserService {


    @PostMapping("/test/str")
    String testStr() throws Exception;


}
