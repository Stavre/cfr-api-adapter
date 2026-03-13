package com.stavre.cfrapiadapter.dto.enriched;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;

import java.util.List;
import java.util.Map;

public record EnrichedTrainDto(
        EnrichedTrainMetadataDto metadata,
        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops) {
}
