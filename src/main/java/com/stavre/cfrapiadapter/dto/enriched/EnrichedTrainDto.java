package com.stavre.cfrapiadapter.dto.enriched;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import java.util.List;
import java.util.Map;

public record EnrichedTrainDto(
        TrainMetadataDto metadata,
        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops) {}
