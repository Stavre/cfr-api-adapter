package com.stavre.cfrapiadapter.dto;

public record TrainStopDto(
        String departureTime,
        String arrivalTime,
        String stationName,
        String stationHref,
        String km,
        String stopDuration,
        String platform,
        String statusLabel) { }
