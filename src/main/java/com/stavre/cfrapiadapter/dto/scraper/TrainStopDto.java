package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;
import java.util.List;

@Builder
public record TrainStopDto(
        String arrivalTime,
        String arrivalTimeLabel,
        String departureTime,
        String departureTimeLabel,
        String stationName,
        List<String> stationLabels,
        String km,
        String stopDuration,
        String platform) {}
