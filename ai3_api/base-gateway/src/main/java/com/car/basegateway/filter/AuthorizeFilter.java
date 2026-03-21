package com.car.basegateway.filter;

import com.ai.basecommon.utils.JwtUtil;
import com.ai.basecommon.utils.LogUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();

        String token = serverHttpRequest.getHeaders().getFirst("X-Token");

        Long userId = null;

        if(null != token && !token.isEmpty()){
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            Long id = JwtUtil.getUserIdFromToken(token);
            if(null != id && id > 0){
                userId = id;
            }
        }

        if(null != userId){
            mutate.header( "X-UserId", userId.toString());
            mutate.header( "X-Token", token);
        }
        else{
            mutate.header( "X-UserId", "");
            mutate.header( "X-Token", "");
        }


        //LogUtil.log("网关解析出用户ID是：" + userId);

        String referer = serverHttpRequest.getHeaders().getFirst("Referer");
        String userAgent = serverHttpRequest.getHeaders().getFirst("User-Agent");

        boolean isSwaggerRequest = (referer != null && referer.contains("/swagger-ui/")) ||
                (userAgent != null && userAgent.toLowerCase().contains("swagger"));

        if (isSwaggerRequest) {
            mutate.header("X-From-Swagger", "true");
        } else {
            mutate.header("X-From-Swagger", "false");
        }


        // 获取客户端IP地址
        Optional.of(exchange.getRequest()).ifPresent(item -> {
            List<String> xForwardedFor = item.getHeaders().get("x-forwarded-for");
            List<String> xRealIp = item.getHeaders().get("x-real-ip");
            List<String> remoteHost = item.getHeaders().get("remote-host");
            mutate.header("X-Forwarded-For", (xForwardedFor == null || xForwardedFor.isEmpty()) ? "" :
                    xForwardedFor.get(0));
            mutate.header("X-Real-IP", (xRealIp == null || xRealIp.isEmpty()) ? "" : xRealIp.get(0));
            mutate.header("Remote-Host", (remoteHost == null || remoteHost.isEmpty()) ? "" :
                    remoteHost.get(0));
        });


        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }



    @Override
    public int getOrder() {
        return 0;
    }

}
