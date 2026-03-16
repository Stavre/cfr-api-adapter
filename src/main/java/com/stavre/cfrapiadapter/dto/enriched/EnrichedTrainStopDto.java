package com.stavre.cfrapiadapter.dto.enriched;

import lombok.Builder;
import lombok.With;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@With
@Builder
public record EnrichedTrainStopDto(
        LocalDateTime arrival,
        Duration arrivalDelay,
        LocalDateTime departure,
        Duration departureDelay,
        String station,
        Integer journeyKm,
        Duration stopDuration,
        String platform,
        List<String> trainStopMessages,
        List<String> errors
) {
    public EnrichedTrainStopDto(List<String> errors) {
        this(null, null, null, null, null, null, null, null, null, errors);
    }
}
