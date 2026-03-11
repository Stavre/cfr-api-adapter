package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;

import java.time.LocalDateTime;

public class TrainDelayResponseAdapter {

    public TrainDelayResponseDto adapt(EnrichedTrainDto train, String date) {
        LocalDateTime requestedAt = LocalDateTime.now();
        return new TrainDelayResponseDto(
                requestedAt,
                date,
                train.metadata(),
                train.stops().getLast()
        );
    }

    public TrainDelayResponseDto adapt(EnrichedTrainDto train, String date, String stationName) {
        LocalDateTime requestedAt = LocalDateTime.now();
        EnrichedTrainStopDto stop = train.stops().stream().filter(s -> s.station().equals(stationName)).findFirst().orElseThrow();

        return new TrainDelayResponseDto(
                requestedAt,
                date,
                train.metadata(),
                stop
        );
    }
}
