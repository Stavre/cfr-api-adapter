package com.stavre.cfrapiadapter.dto.scraper;

import java.util.List;

public record TrainStopDto(
        String arrivalTime,
        String arrivalTimeLabel,
        String departureTime,
        String departureTimeLabel,
        String stationName,
        List<String> stationLabels,
        String km,
        String stopDuration,
        String platform) {
}
