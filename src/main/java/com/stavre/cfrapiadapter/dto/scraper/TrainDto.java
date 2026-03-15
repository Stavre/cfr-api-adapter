package com.stavre.cfrapiadapter.dto.scraper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TrainDto(
        TrainMetadataDto metadata,
        Map<TrainBranchDto, List<Optional<TrainStopDto>>> branchStops
) {}
