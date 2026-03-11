package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.train.TrainDto;

public class TrainAdapter {
    private final TrainMetadataAdapter trainMetadataAdapter = new TrainMetadataAdapter();
    private final TrainStopAdapter trainStopAdapter = new TrainStopAdapter();

    public EnrichedTrainDto adapt(TrainDto dto, String date) {
        return new EnrichedTrainDto(
                trainMetadataAdapter.adapt(dto.metadata()),
                dto.stops().stream().map(stop -> trainStopAdapter.adapt(stop, date)).toList()
        );
    }
}
