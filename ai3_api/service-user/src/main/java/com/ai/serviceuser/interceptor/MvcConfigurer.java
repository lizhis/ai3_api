package com.ai.serviceuser.interceptor;

import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.mapper.UserLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @Description //注册拦截器
 * @Author  
 *
 */

@Configuration
@EnableWebMvc
public class MvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Bean
    public Interceptor interceptor() {
        return new Interceptor(redisUtilX,userLoginMapper);
    }


    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //给指定url增加拦截器 可将要拦截的Url搭配通配符写在配置文件中 /Index/**   excludePathPatterns方法排除url
        /*registry.addInterceptor(new Interceptor())
                .addPathPatterns("/Index/**")
                .excludePathPatterns("/Index/c")
                .excludePathPatterns("/Index/d")
        ;*/

        //如果你愿意 可以在这注册多个拦截器 拦截不同的路由
        InterceptorRegistration registration = registry.addInterceptor(interceptor()).addPathPatterns("/**");
        registration.excludePathPatterns("/error","/swagger-ui.html","/swagger-resources/**","/webjars/**","/v3/**");
        // registry.addInterceptor(new Interceptor2()).addPathPatterns("/Index2/a");
        // registry.addInterceptor(new Interceptor3()).addPathPatterns("/Index3/a");

    }

    //跨域配置
/*    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT","PATCH")
                .allowedHeaders("*")
                .maxAge(3600)
                .exposedHeaders("Authorization");
    }*/

/*    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }*/



}
