package com.stavre.cfrapiadapter.dto.enriched;

import lombok.Builder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EnrichedTrainDepartureDto(
        LocalDateTime departureTimestamp,
        Duration departureDelay,
        String platform,
        String toStation,
        EnrichedTrainMetadataDto train,
        List<String> mainStations,
        Duration stopDuration) {}
