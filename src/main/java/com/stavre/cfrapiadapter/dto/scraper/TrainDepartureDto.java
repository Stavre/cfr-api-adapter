package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;

@Builder
public record TrainDepartureDto(
        String departureTime,
        String departureTimeLabel,
        String platform,
        String destinationName,
        TrainMetadataDto train,
        String mainStations,
        String stopLabel
) {}
