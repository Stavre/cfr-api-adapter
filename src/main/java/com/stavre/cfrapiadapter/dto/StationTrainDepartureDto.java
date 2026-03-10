package com.stavre.cfrapiadapter.dto;

public record StationTrainDepartureDto (
    String departureTime,    // "5:18"
    String destinationName,  // "București Obor"
    String destinationHref,  // "/ro-RO/Statie/..."
    TrainDto train,
    String stopDuration,     // "1 min (începând cu 5:17)"
    String delayLabel,       // "+11 min (întârziere)" or "la timp*"
    String platform         // "linia 1", "linia 3", etc.
) { }
