package com.stavre.cfrapiadapter.dto.train;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TrainDto(
        Optional<TrainMetadataDto> metadata,
        Map<String, List<Optional<TrainStopDto>>> branchStops
) {
}
