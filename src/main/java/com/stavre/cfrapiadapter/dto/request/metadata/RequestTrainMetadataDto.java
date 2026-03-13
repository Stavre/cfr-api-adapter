package com.stavre.cfrapiadapter.dto.request.metadata;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RequestTrainMetadataDto(
        LocalDateTime requestedAt,
        String requestedFor,
        String requestedTrainNumber
) {
    public RequestTrainMetadataDto(String requestedFor, String requestedTrainNumber) {
        this(LocalDateTime.now(), requestedFor, requestedTrainNumber);
    }

}
