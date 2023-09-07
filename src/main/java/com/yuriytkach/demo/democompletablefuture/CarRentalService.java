package com.yuriytkach.demo.democompletablefuture;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarRentalService {

  private final Executor myExecutor;

  @Cacheable("car-rentals")
  public CompletableFuture<List<CarRental>> findCarRentals(final String airport, final LocalDate localDate, final int nights) {
    log.info("Find car rentals in {} on {} for {} nights", airport, localDate, nights);

    return CompletableFuture.supplyAsync(() -> List.of(
      new CarRental("Hertz", null, airport, "Toyota", 2000),
      new CarRental("Avis", null, airport, "Honda", 3000)
    ), myExecutor);
  }
}
