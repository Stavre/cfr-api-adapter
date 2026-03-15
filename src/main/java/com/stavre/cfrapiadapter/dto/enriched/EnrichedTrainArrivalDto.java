package com.stavre.cfrapiadapter.dto.enriched;

import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.Builder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EnrichedTrainArrivalDto(
        LocalDateTime arrivalTimestamp,
        Duration arrivalDelay,
        String platform,
        String fromStation,
        TrainMetadataDto train,
        List<String> mainStations,
        Duration stopDuration
) {}
