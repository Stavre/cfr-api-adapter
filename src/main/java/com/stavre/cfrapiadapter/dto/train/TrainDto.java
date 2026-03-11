package com.stavre.cfrapiadapter.dto.train;

import java.util.List;
import java.util.Optional;

public record TrainDto(
        Optional<TrainMetadataDto> metadata,
        List<Optional<TrainStopDto>> stops
) {
}
