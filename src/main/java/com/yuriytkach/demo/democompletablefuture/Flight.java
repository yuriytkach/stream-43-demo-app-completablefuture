package com.yuriytkach.demo.democompletablefuture;

public record Flight(
  String departureAirport,
  String arrivalAirport,
  String departureTime,
  String arrivalTime,
  String flightNumber,
  int price
) {

}
