package com.car.basegateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SwaggerConfigController {

    @GetMapping("/swagger-config")
    public Map<String, Object> swaggerConfig() {
        Map<String, Object> config = new HashMap<>();
        List<Map<String, String>> urls = new ArrayList<>();

        urls.add(Map.of("name", "service-base", "url", "/v3/api-docs/base"));
        urls.add(Map.of("name", "service-user", "url", "/v3/api-docs/user"));
        urls.add(Map.of("name", "service-business", "url", "/v3/api-docs/business"));
        urls.add(Map.of("name", "service-customer", "url", "/v3/api-docs/customer"));

        config.put("urls", urls);
        return config;
    }

}
