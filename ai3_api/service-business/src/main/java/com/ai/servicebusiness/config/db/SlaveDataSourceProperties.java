package com.ai.servicebusiness.config.db;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.slave")
public class SlaveDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private HikariConfig hikari = new HikariConfig();
}