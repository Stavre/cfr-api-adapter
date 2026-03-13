package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataDto;

import java.util.List;

public record StationResponseDto(
        RequestStationMetadataDto request,
        List<EnrichedStationTrainDto> trains
) {
}
