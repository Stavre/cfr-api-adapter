package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
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

    public Optional<EnrichedTrainArrivalDto> adapt(TrainArrivalDepartureDto arrival, String date) {
        if (arrival == null) {
            return Optional.empty();
        }
        List<String> errors = new ArrayList<>();

        EnrichedTrainArrivalDto enrichedArrival = EnrichedTrainArrivalDto.builder()
                .arrivalTimestamp(utils.getTimestamp(date, arrival.time(), errors))
                .arrivalDelay(utils.getDelay(arrival.timeLabel(), errors))
                .platform(utils.getTrainPlatform(arrival.platform(), errors))
                .fromStation(arrival.otherStation())
                .train(arrival.train())
                .mainStations(utils.getDirection(arrival.mainStations(), errors))
                .stopDuration(utils.getStopDuration(arrival.stopLabel(), errors))
                .build();
        
        return Optional.of(enrichedArrival);
    }
}
