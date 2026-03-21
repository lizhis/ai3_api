package com.ai.servicebase.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("综合服务 API"));
    }

    @Bean
    @Profile("test")
    public OpenApiCustomizer serverSchemeCustomizer() {
        return openApi -> {
            if (openApi.getServers() != null) {
                openApi.getServers().forEach(server -> {
                    String url = server.getUrl();
                    if (url.startsWith("http://")) {
                        server.setUrl("https://" + url.substring("http://".length()));
                    }
                });
            }
        };
    }

}
