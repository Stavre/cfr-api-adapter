package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestTrainMetadataDto;

public record TrainResponseDto(
        RequestTrainMetadataDto request,
        EnrichedTrainDto train
) {
}
