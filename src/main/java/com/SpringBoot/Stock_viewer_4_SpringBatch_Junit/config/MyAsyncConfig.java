package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync(proxyTargetClass = true) 
public class MyAsyncConfig {

    @Bean(name ="myTaskExecutor")
    public TaskExecutor myTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  //2
        executor.setMaxPoolSize(10);      //2
        executor.setQueueCapacity(100);
        
        executor.setThreadNamePrefix("myThread-");
        executor.initialize();
        return executor;
    }
}
