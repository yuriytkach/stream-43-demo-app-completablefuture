package com.yuriytkach.demo.democompletablefuture;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelService {

  private final CustomerService customerService;
  private final AirportService airportService;
  private final FlightService flightService;
  private final HotelService hotelService;
  private final CarRentalService carRentalService;
  private final TravelPackageService travelPackageService;

  private final Executor myExecutor;

  public CompletableFuture<List<TravelPackage>> findTravel(
    final String arrivalCity,
    final String userId,
    final LocalDate localDate,
    final int nights
  ) {
    MDC.put("user-id", userId);
    log.info("Find travel for user {} to {} on {}", userId, arrivalCity, localDate);

    final CompletableFuture<List<Hotel>> hotelsFt = hotelService.findHotels(arrivalCity, localDate, nights);

    final CompletableFuture<Customer> customerFt = customerService.findById(userId);

    final CompletableFuture<List<String>> nearbyAirportsFt = customerFt.thenComposeAsync(
      customer -> airportService.findNearbyAirports(customer.homeCity()), myExecutor);


    final CompletableFuture<List<Flight>> flightsFt = nearbyAirportsFt.thenComposeAsync(nearbyAirports -> {
      final List<CompletableFuture<List<Flight>>> futures = nearbyAirports.stream()
        .map(departureAirport -> flightService.findFlights(departureAirport, arrivalCity, localDate))
        .toList();

      return mergeFutures(futures);
    }, myExecutor);

    final CompletableFuture<List<CarRental>> carsFt = flightsFt.thenComposeAsync(flights -> {
      final List<CompletableFuture<List<CarRental>>> futures = flights.stream()
        .map(Flight::arrivalAirport)
        .distinct()
        .map(airport -> carRentalService.findCarRentals(airport, localDate, nights))
        .toList();

      return mergeFutures(futures);
    }, myExecutor);

    return flightsFt.thenCombine(hotelsFt, FlightHotelHolder::new)
      .thenCombine(carsFt, (fhh, cars) -> travelPackageService.createPackages(fhh.flights(), fhh.hotels(), cars, nights));
  }

  private <T> CompletionStage<List<T>> mergeFutures(final List<CompletableFuture<List<T>>> futures) {
    return futures.stream()
      .reduce((f1, f2) -> f1.thenCombine(f2, (list1, list2) ->
        Stream.concat(list1.stream(), list2.stream()).toList()))
      .orElseGet(() -> CompletableFuture.completedFuture(List.of()));
  }

  private record FlightHotelHolder(List<Flight> flights, List<Hotel> hotels) {

  }
}
