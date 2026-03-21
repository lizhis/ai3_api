package com.ai.serviceuser.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Description 异步线程池
 * @Author  
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${thread.pool.corePoolSize:10}")
    private int corePoolSize;

    @Value("${thread.pool.maxPoolSize:20}")
    private int maxPoolSize;

    @Value("${thread.pool.queueCapacity:512}")
    private int queueCapacity;

    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);//核心线程数

        executor.setMaxPoolSize(maxPoolSize);//最大线程数

        executor.setQueueCapacity(queueCapacity);//缓冲队列线程数

        executor.setThreadNamePrefix("Async-Thread-");

        //该方法用来设置线程池关闭的时候等待所有任务都完成后,再继续销毁其他的Bean，这样这些异步任务的销毁就会先于数据库连接池对象的销毁。
        executor.setWaitForTasksToCompleteOnShutdown(true);

        //线程池中任务的等待时间,如果超过这个时间还没有销毁就强制销毁,以确保应用最后能够被关闭,而不是阻塞住。
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            System.err.println("异步线程报错: " + method.getName());
            throwable.printStackTrace();
        };
    }
}
