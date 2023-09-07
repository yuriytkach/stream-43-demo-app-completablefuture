package com.yuriytkach.demo.democompletablefuture;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RetryConfiguration {

  @Bean
  RetryRegistry retryRegistry() {
    final RetryConfig config = RetryConfig.custom()
      .maxAttempts(3)
      .waitDuration(Duration.ofMillis(600))
      .retryExceptions(TimeoutException.class, NullPointerException.class)
      .failAfterMaxAttempts(true)
      .build();

    return RetryRegistry.of(config);
  }

  @Bean
  Retry myRetry(final RetryRegistry retryRegistry) {
    final Retry retry = retryRegistry.retry("my-retry");
    retry.getEventPublisher()
      .onRetry(event -> log.warn("Retry attempt: {}", event.getNumberOfRetryAttempts()))
      .onSuccess(event -> log.warn("Retry succeeded after: {} attempts", event.getNumberOfRetryAttempts()))
      .onError(event -> log.warn("Retry failed after: {} attempts", event.getNumberOfRetryAttempts()));
    return retry;
  }
}
