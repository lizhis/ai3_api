package com.ai.basead.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("综合服务 API")
                        //.description("用户服务API.")
                        //.version("v1.0")
                        //.license(new License().name("Apache2.0").url("http://springdoc.org"))
                )
                //.externalDocs(new ExternalDocumentation().description("Documentation").url("https://www.jianshu.com/nb/41542276"))
                ;
    }

}
