package com.stavre.cfrapiadapter.dto.scraper;

public record StationTrainDto(
    String time,
    String timeLabel,
    String platform,
    String secondStation,  // TODO: Find better name
    TrainMetadataDto train,
    String mainStations,
    String stopDuration
) { }
