package com.ai.servicebase.service;

import com.ai.basecommon.core.vo.shop.ShopVO;
import com.ai.servicebase.service.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "service-energy",configuration = FeignConfig.class)
public interface IEnergyService {


    @PostMapping("/shopApi/selectRecommend")
    List<ShopVO> selectRecommend() throws Exception;


}
