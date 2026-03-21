package com.car.basegateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("API 文档"))
                .servers(List.of(
                        new Server().url("/").description("via Gateway")
                ));
    }

/*

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        definitions.stream()
                .filter(routeDefinition -> routeDefinition.getId().matches("service-.*"))
                .forEach(routeDefinition -> {
                    String name = routeDefinition.getId().replace("service-", "");
                    GroupedOpenApi group = GroupedOpenApi.builder()
                            .pathsToMatch("/" + name + "/**")
                            .group(name)
                            .addOpenApiCustomizer(gatewayServersCustomizer())
                            .build();

                    groups.add(group);
                });


        return groups;
    }

*/


}
