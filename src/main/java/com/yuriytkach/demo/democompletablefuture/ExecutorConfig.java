package com.yuriytkach.demo.democompletablefuture;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.core.ContextAwareScheduledThreadPoolExecutor;

@Configuration
public class ExecutorConfig {

  @Bean
  public Executor myExecutor() {
    return new ContextAwareScheduledThreadPoolExecutor.Builder().corePoolSize(10).build();
  }

}
