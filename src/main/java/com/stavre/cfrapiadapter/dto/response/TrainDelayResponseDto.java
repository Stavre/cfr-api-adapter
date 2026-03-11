package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import java.time.LocalDateTime;

public record TrainDelayResponseDto(
        LocalDateTime requestedAt,
        String forDate,
        EnrichedTrainMetadataDto train,
        EnrichedTrainStopDto stop) {
}
