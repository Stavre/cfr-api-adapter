package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnrichedTrainArrivalAdapter {
    
    private final AdapterUtils utils;
    private final TrainMetadataAdapter trainMetadataAdapter;
    
    public Optional<EnrichedTrainArrivalDto> adapt(Optional<TrainArrivalDto> arrival, String date) {
        if (arrival.isEmpty()) {
            return Optional.empty();
        }
        List<String> errors = new ArrayList<>();
        TrainArrivalDto trainArrival = arrival.get();
        
        EnrichedTrainArrivalDto enrichedArrival = EnrichedTrainArrivalDto.builder()
                .arrivalTimestamp(utils.getTimestamp(date, trainArrival.arrivalTime(), errors))
                .arrivalDelay(utils.getDelay(trainArrival.arrivalTimeLabel(), errors))
                .platform(utils.getTrainPlatform(trainArrival.platform(), errors))
                .fromStation(trainArrival.originStation())
                .train(trainMetadataAdapter.adapt(trainArrival.train()))
                .mainStations(utils.getDirection(trainArrival.mainStations(), errors))
                .stopDuration(utils.getStopDuration(trainArrival.stopLabel(), errors))
                .build();
        
        return Optional.of(enrichedArrival);
    }
}
