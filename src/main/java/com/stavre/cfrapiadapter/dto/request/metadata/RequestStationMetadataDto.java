package com.stavre.cfrapiadapter.dto.request.metadata;

import com.stavre.cfrapiadapter.dto.enriched.StationTrainType;

import java.time.LocalDateTime;

public record RequestStationMetadataDto(
        LocalDateTime requestedAt,
        String requestedFor,
        String requestedStation,
        StationTrainType type
) {
    public RequestStationMetadataDto(String requestedFor, String requestedStation, StationTrainType type) {
        this(LocalDateTime.now(), requestedFor, requestedStation, type);
    }
}
