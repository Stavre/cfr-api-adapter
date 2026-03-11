package com.stavre.cfrapiadapter.dto.train;

public record TrainArrivalDto(
    String arrivalTime,    // "5:18"
    String arrivalTimeLabel,  // "+11 min (întârziere)" or "la timp*"
    String platform,            // "linia 1", "linia 3", etc.
    String originStation,  // "București Obor"
    TrainMetadataDto train,
    String mainStations,
    String stopDuration     // "1 min (începând cu 5:17)"
) { }
