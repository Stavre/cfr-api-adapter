package com.stavre.cfrapiadapter.dto.enriched;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record EnrichedTrainDto(
        TrainMetadataDto metadata,
        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops) {}
