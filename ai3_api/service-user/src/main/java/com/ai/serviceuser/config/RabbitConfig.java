package com.ai.serviceuser.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue adActive() {
        return new Queue("ad_active",true);
    }





    @Bean("adActiveContainerFactory")
    public SimpleRabbitListenerContainerFactory userAssetTrendsContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(2);
        factory.setConcurrentConsumers(3);
        configurer.configure(factory, connectionFactory);
        return factory;
    }





}
