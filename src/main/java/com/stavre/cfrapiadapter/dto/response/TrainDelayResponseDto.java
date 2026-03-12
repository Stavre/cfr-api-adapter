package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import java.time.LocalDateTime;
import java.util.Map;

public record TrainDelayResponseDto(
        LocalDateTime requestedAt,
        String forDate,
        EnrichedTrainMetadataDto train,
        Map<String, EnrichedTrainStopDto> branchStop) {
}
