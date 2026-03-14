package com.stavre.cfrapiadapter.dto.request.metadata;

import java.time.LocalDateTime;

public record RequestStationMetadataDto(
        LocalDateTime requestedAt,
        String requestedFor,
        String requestedStation
) {
    public RequestStationMetadataDto(String requestedFor, String requestedStation) {
        this(LocalDateTime.now(), requestedFor, requestedStation);
    }
}