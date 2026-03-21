package com.ai.basead.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencent")
public class TencentProperties {

    private String accountId1;
    private String dataSourceId1;
    private String accessToken1;

    private String accountId2;
    private String dataSourceId2;
    private String accessToken2;

    private String accountId3;
    private String dataSourceId3;
    private String accessToken3;

    private String accountId4;
    private String dataSourceId4;
    private String accessToken4;

}