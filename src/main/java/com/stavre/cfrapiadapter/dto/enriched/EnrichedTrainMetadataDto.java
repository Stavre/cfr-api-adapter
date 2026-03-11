package com.stavre.cfrapiadapter.dto.enriched;

import java.util.List;

public record EnrichedTrainMetadataDto(String id,
                                       String category,
                                       String number,
                                       String operator,
                                       List<String> errors) {
    public EnrichedTrainMetadataDto(List<String> errors) {
        this(null, null, null, null, errors);
    }
}
