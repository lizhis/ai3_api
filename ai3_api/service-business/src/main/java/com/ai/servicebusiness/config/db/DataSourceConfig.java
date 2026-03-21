package com.ai.servicebusiness.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class DataSourceConfig {

    private final MasterDataSourceProperties masterProps;
    private final SlaveDataSourceProperties slaveProps;

    private final AtomicBoolean dynamicRwSwitch = new AtomicBoolean();

    private DynamicRoutingDataSource routingDataSource;

    private volatile DataSource slaveDataSource;

    private final boolean initialRwEnabled;


    public DataSourceConfig(MasterDataSourceProperties masterProps,
                            SlaveDataSourceProperties slaveProps,
                            @Value("${rw.separation.enabled:true}") boolean enabled) {
        this.masterProps = masterProps;
        this.slaveProps = slaveProps;
        this.dynamicRwSwitch.set(enabled);
        this.initialRwEnabled = enabled;
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        HikariConfig config = masterProps.getHikari();
        config.setJdbcUrl(masterProps.getUrl());
        config.setUsername(masterProps.getUsername());
        config.setPassword(masterProps.getPassword());
        config.setDriverClassName(masterProps.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean(name = "slaveDataSource")
    @Lazy
    public DataSource slaveDataSource() {
        if (this.slaveDataSource != null) {
            return this.slaveDataSource;
        }

        HikariDataSource ds = buildSlaveDataSource();
        if (ds != null) {
            this.slaveDataSource = ds;
        }
        return ds;
    }


    private HikariDataSource buildSlaveDataSource() {
        HikariConfig config = slaveProps.getHikari();
        config.setJdbcUrl(slaveProps.getUrl());
        config.setUsername(slaveProps.getUsername());
        config.setPassword(slaveProps.getPassword());
        config.setDriverClassName(slaveProps.getDriverClassName());

        try {
            HikariDataSource ds = new HikariDataSource(config);
            try (Connection conn = ds.getConnection()) {
                return ds;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 从库连接失败：" + e.getMessage());
            dynamicRwSwitch.set(false);
            return null;
        }
    }

    @Bean
    @Primary
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource master) {
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("master", master);

        if (initialRwEnabled) {
            DataSource slave = slaveDataSource();
            if (slave != null) {
                dataSources.put("slave", slave);
            }
        }

        DynamicRoutingDataSource ds = new DynamicRoutingDataSource(dynamicRwSwitch::get);
        ds.setDefaultTargetDataSource(master);
        ds.setTargetDataSources(dataSources);
        ds.afterPropertiesSet();
        this.routingDataSource = ds;
        return ds;
    }


    public void tryInitSlaveIfAbsent() {
        if (this.slaveDataSource == null) {
            HikariDataSource ds = buildSlaveDataSource();
            if (ds != null) {
                this.slaveDataSource = ds;
                dynamicRwSwitch.set(true);

                Map<Object, Object> targetMap = new HashMap<>(this.routingDataSource.getResolvedDataSources());
                targetMap.put("slave", ds);
                this.routingDataSource.setTargetDataSources(targetMap);
                this.routingDataSource.afterPropertiesSet();

                System.out.println("✅ 手动初始化从库成功，读写分离已启用");
            } else {
                System.err.println("⚠️ 手动初始化从库失败，仍然使用主库");
            }
        } else {
            dynamicRwSwitch.set(true);
        }
    }
    public AtomicBoolean getRwSwitch() {
        return dynamicRwSwitch;
    }

}