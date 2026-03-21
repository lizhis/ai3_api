package com.ai.servicebase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.ai.servicebase.mapper")
public class ServiceBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceBaseApplication.class, args);
	}

}
