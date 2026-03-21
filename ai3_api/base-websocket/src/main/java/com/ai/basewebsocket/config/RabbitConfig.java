package com.ai.basewebsocket.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {



    @Bean("wsContainerFactory")
    public SimpleRabbitListenerContainerFactory wsContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(2);
        factory.setConcurrentConsumers(3);
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    @Bean("wsAiChatContainerFactory")
    public SimpleRabbitListenerContainerFactory wsAiChatContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(2);
        factory.setConcurrentConsumers(3);
        configurer.configure(factory, connectionFactory);
        return factory;
    }




}
