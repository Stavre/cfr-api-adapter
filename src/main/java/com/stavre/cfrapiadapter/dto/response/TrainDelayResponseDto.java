package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import java.util.Map;

public record TrainDelayResponseDto(
        TrainMetadataDto metadata,
        Map<TrainBranchDto, EnrichedTrainStopDto> branchStop) {
}
