package com.stavre.cfrapiadapter.dto.train;

public record StationTrainDto(
    String time,
    String timeLabel,
    String platform,
    String secondStation,  // TODO: Find better name
    TrainMetadataDto train,
    String mainStations,
    String stopDuration
) { }
