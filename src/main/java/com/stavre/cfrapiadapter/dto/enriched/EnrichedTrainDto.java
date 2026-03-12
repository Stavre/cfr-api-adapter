package com.stavre.cfrapiadapter.dto.enriched;

import java.util.List;
import java.util.Map;

public record EnrichedTrainDto(
        EnrichedTrainMetadataDto metadata,
        Map<String, List<EnrichedTrainStopDto>> stops) {
}
