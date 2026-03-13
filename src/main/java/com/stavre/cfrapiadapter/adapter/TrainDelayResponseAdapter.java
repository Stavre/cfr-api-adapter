package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainDelayResponseAdapter {

    public TrainDelayResponseDto adapt(EnrichedTrainDto train, String date) {
        LocalDateTime requestedAt = LocalDateTime.now();
        Map<TrainBranchDto, EnrichedTrainStopDto> branchStops = train.stops().
                entrySet().stream().
                map(s -> Map.entry(s.getKey(), s.getValue().getLast()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new TrainDelayResponseDto(
                requestedAt,
                date,
                train.metadata(),
                branchStops
        );
    }

    public TrainDelayResponseDto adapt(EnrichedTrainDto train, String date, String stationName) {
        LocalDateTime requestedAt = LocalDateTime.now();
        Map<TrainBranchDto, EnrichedTrainStopDto> branchStops = train.stops().
                entrySet().stream()
                .filter(s -> s.getValue().stream().anyMatch(stop -> stop.station().equals(stationName)))
                .map(s -> Map.entry(s.getKey(), s.getValue().stream().filter(st -> st.station().equals(stationName)).findFirst().orElseThrow()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new TrainDelayResponseDto(
                requestedAt,
                date,
                train.metadata(),
                branchStops
        );
    }
}
//pisu