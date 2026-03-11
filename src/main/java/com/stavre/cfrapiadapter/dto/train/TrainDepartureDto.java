package com.stavre.cfrapiadapter.dto.train;

public record TrainDepartureDto(
    String departureTime,    // "5:18"
    String departureTimeLabel,  // "+11 min (întârziere)" or "la timp*"
    String platform,            // "linia 1", "linia 3", etc.
    String destinationName,  // "București Obor"
    TrainDto train,
    String mainStations,
    String stopDuration     // "1 min (începând cu 5:17)"
) { }
