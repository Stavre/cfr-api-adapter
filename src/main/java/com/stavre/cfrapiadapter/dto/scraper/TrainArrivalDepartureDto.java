package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;


/**
 * Class for modeling train departures and train arrivals for a station
 */

@Builder
public record TrainArrivalDepartureDto(
        String time,
        String timeLabel,
        String platform,
        String otherStation,
        TrainMetadataDto train,
        String mainStations,
        String stopLabel
) {}
