package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;

@Builder
public record TrainMetadataDto(
        String id,
        String number,
        String category,
        String operator) {}
