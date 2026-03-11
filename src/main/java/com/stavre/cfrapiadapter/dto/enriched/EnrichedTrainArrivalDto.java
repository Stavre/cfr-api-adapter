package com.stavre.cfrapiadapter.dto.enriched;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record EnrichedTrainArrivalDto(
        LocalDateTime arrival,
        Duration arrivalDelay,
        String platform,
        String origin,
        EnrichedTrainDto train,
        List<String> direction,
        Duration stopDuration,
        LocalDateTime stopEndsAt,
        List<String> errors
) {
    public EnrichedTrainArrivalDto(List<String> errors) {
        this(null, null, null, null, null, null, null, null, errors);
    }
}
