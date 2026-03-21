package com.ai.basead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BaseAdApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseAdApplication.class, args);
	}

}
