package com.stavre.cfrapiadapter.dto.enriched;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record EnrichedTrainDepartureDto(
        LocalDateTime departure,
        Duration departureDelay,
        String platform,
        String destination,
        EnrichedTrainMetadataDto train,
        List<String> direction,
        Duration stopDuration,
        LocalDateTime stopStartsAt,
        List<String> errors
) {
    public EnrichedTrainDepartureDto(List<String> errors) {
        this(null, null, null, null, null, null, null, null, errors);
    }
}
