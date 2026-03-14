package com.stavre.cfrapiadapter.dto.enriched;

import lombok.Builder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EnrichedStationTrainDto(
        String fromStation,
        LocalDateTime arrival,
        Duration arrivalDelay,

        String toStation,
        LocalDateTime departure,
        Duration departureDelay,

        Duration stopDuration,
        EnrichedTrainMetadataDto train,
        String platform,
        List<String> direction,
        List<String> errors
) {
    public EnrichedStationTrainDto(List<String> errors) {
        this(null, null, null, null, null, null, null, null, null, null, errors);
    }
}
