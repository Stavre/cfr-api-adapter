package com.stavre.cfrapiadapter.dto.response;

import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataDto;
import java.time.Duration;

public record StationTotalDelayResponseDto(
        RequestStationMetadataDto request,
        Duration delay
) {}
