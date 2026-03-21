package com.ai.servicebusiness.service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @Description
 * @Author  
 */
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletRequest request = attributes.getRequest();

        //添加token
        //requestTemplate.header("Authorization", request.getHeader("Authorization"));
        requestTemplate.header("service", "1");
    }

}

