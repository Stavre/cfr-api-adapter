package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;

import java.util.List;

public record StationResponseDto(
        List<EnrichedStationTrainDto> trains
) {}
