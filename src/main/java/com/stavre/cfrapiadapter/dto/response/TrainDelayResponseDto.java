package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import java.time.LocalDateTime;
import java.util.Map;

public record TrainDelayResponseDto(
        LocalDateTime requestedAt,
        String forDate,
        TrainMetadataDto train,
        Map<TrainBranchDto, EnrichedTrainStopDto> branchStop) {
}
