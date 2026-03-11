package com.stavre.cfrapiadapter.dto.enriched;

import java.util.List;

public record EnrichedTrainDto(String id,
                               String category,
                               String number,
                               String operator,
                               List<String> errors) {
    public EnrichedTrainDto(List<String> errors) {
        this(null, null, null, null, errors);
    }
}
