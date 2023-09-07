package com.yuriytkach.demo.democompletablefuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

  private final Executor myExecutor;

  public CompletableFuture<List<String>> findNearbyAirports(final String city) {
    log.info("Find nearby airports for city {}", city);
    return CompletableFuture.supplyAsync(() -> List.of("JFK", "EWR", "LGA"), myExecutor);
  }
}
