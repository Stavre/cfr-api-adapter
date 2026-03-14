package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;

@Builder
public record TrainArrivalDto(
        String arrivalTime,
        String arrivalTimeLabel,
        String platform,
        String originStation,
        TrainMetadataDto train,
        String mainStations,
        String stopLabel
) {}