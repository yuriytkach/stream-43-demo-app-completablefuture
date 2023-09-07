package com.yuriytkach.demo.democompletablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

  private final Executor myExecutor;

  public CompletableFuture<Customer> findById(final String userId) {
    log.info("Find customer by id {}", userId);

    return CompletableFuture.supplyAsync(() -> new Customer(userId, "John", "Doe", "New York", true), myExecutor);
  }
}
