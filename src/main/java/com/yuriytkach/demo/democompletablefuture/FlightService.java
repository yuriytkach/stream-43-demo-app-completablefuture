package com.yuriytkach.demo.democompletablefuture;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {
  private final Executor myExecutor;
  private final CarRentalService carRentalService;

  private final ConcurrentHashMap<String, CompletableFuture<List<CarRental>>> map = new ConcurrentHashMap<>();

  public CompletableFuture<List<Flight>> findFlights(final String departureAirport, final String arrivalCity, final LocalDate localDate) {
    log.info("Find flights from {} to {} on {}", departureAirport, arrivalCity, localDate);

    final var future1 = CompletableFuture.supplyAsync(() -> List.of(
      new Flight(departureAirport, "LND", "13:00", "15:00", "AADDDBBB", 3000)
    ), myExecutor);

    final var future2 = CompletableFuture.supplyAsync(() -> List.of(
      new Flight(departureAirport, "MSX", "22:00", "15:00", "8234324", 6000)
    ), myExecutor);

    final CompletableFuture<List<Flight>> result = CompletableFuture.anyOf(future1, future2)
      .thenApplyAsync(o -> (List<Flight>) o, myExecutor);

    return result;
  }

  private Object findCars(final String airport) {
    if (map.containsKey(airport)) {
      return map.get(airport);
    } else {
      final CompletableFuture<List<CarRental>> carRentals = carRentalService.findCarRentals(airport, null, 10);
      map.put(airport, carRentals);
      return carRentals;
    }
  }
}
