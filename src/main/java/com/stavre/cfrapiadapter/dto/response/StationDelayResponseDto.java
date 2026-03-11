package com.stavre.cfrapiadapter.dto.response;

import java.time.Duration;
import java.time.LocalDateTime;

public record StationDelayResponseDto(
        LocalDateTime requestedAt,
        String forDate,
        String station,
        Duration totalDelay
) { }
