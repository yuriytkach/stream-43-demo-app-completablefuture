package com.yuriytkach.demo.democompletablefuture;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelPackageService {

  public List<TravelPackage> createPackages(
    final List<Flight> flights,
    final List<Hotel> hotels,
    final List<CarRental> cars,
    final int nights
  ) {
    log.info("Create travel packages from flights: {}, hotels: {}, cars: {}, nights: {}",
      flights.size(), hotels.size(), cars.size(), nights);

    final var hotelsDef = hotels.isEmpty() ? List.of(new Hotel("", 0, 0)) : hotels;

    return flights.stream()
      .flatMap(flight -> hotelsDef.stream()
        .flatMap(hotel -> cars.stream()
          .map(car ->
            new TravelPackage(
              flight,
              hotel,
              car,
              flight.price() + hotel.pricePerNight() * nights + car.pricePerDay() * nights
            )
          )
        ))
      .sorted(Comparator.comparing(TravelPackage::totalPrice))
      .toList();
  }
}
