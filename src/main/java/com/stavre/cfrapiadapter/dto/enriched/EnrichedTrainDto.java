package com.stavre.cfrapiadapter.dto.enriched;

import java.util.List;

public record EnrichedTrainDto(EnrichedTrainMetadataDto metadata, List<EnrichedTrainStopDto> stops) {
}
