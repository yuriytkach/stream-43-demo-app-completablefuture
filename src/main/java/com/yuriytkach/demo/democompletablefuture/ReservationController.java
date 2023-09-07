package com.yuriytkach.demo.democompletablefuture;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final TravelService travelService;

  @GetMapping("/travels/{arrivalCity}/{userId}")
  public ResponseEntity<List<TravelPackage>> findTravels(
    @PathVariable final String arrivalCity,
    @PathVariable final String userId,
    @RequestParam(required = false) final LocalDate travelDate,
    @RequestParam(required = false, defaultValue = "10") final int nights
  ) {
    log.info("Find travels to {} for user {}", arrivalCity, userId);

    final var result = travelService
      .findTravel(arrivalCity, userId, travelDate == null ? LocalDate.now() : travelDate, nights);

    return ResponseEntity.ok(result.join());
  }

}
