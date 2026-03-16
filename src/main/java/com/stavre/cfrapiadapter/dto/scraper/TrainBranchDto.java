package com.stavre.cfrapiadapter.dto.scraper;

import lombok.Builder;

@Builder
public record TrainBranchDto(
        String name,
        String originStation,
        String destinationStation
) {}
