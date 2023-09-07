package com.yuriytkach.demo.democompletablefuture;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import io.github.resilience4j.core.ContextAwareScheduledThreadPoolExecutor;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

  private final Executor myExecutor;
  private final Retry retry;

  public CompletableFuture<List<Hotel>> findHotels(final String arrivalCity, final LocalDate localDate, final int nights) {
    log.info("Find hotels in {} on {} for {} nights", arrivalCity, localDate, nights);

    final CompletableFuture<List<Hotel>> future = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("Thread interrupted", e);
      }
      return List.of(
        new Hotel("Hilton", 5, 5000),
        new Hotel("Marriott", 2, 4000)
      );
    }, myExecutor);

    final CompletableFuture<List<Hotel>> finalFuture = future.orTimeout(500, TimeUnit.MILLISECONDS)
      .whenComplete((hotels, ex) -> {
        if (ex == null) {
          log.info("Hotels found: {}", hotels.size());
        } else {
          log.error("Timeout exception", ex);
        }
      });

    final var retryExecutor = new ContextAwareScheduledThreadPoolExecutor.Builder().corePoolSize(5).build();

    final var retriedFuture = Retry.decorateCompletionStage(retry, retryExecutor, () -> finalFuture)
      .get()
      .toCompletableFuture();

    return retriedFuture
//      .handle((hotels, ex) -> {
//        if (ex != null || hotels.isEmpty()) {
//          return CompletableFuture.supplyAsync(() -> findAlternativeHotels(arrivalCity, localDate, nights));
//        }
//        return hotels;
//      });
      .exceptionally(e -> {
        log.info("Return default empty hotels");
        return List.of();
      });
  }
}
