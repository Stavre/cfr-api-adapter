package com.stavre.cfrapiadapter.dto.enriched;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record EnrichedStationTrainDto(
        StationTrainType type,
        LocalDateTime time,
        Duration delay,
        String platform,
        String secondStation,
        EnrichedTrainMetadataDto train,
        List<String> direction,
        Duration stopDuration,
        LocalDateTime stopStartsAt,
        List<String> errors
) {
    public EnrichedStationTrainDto(StationTrainType type, List<String> errors) {
        this(type, null, null, null, null, null, null, null, null, errors);
    }
}
