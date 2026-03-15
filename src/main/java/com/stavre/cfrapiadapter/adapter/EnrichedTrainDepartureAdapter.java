package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnrichedTrainDepartureAdapter {

    private final AdapterUtils utils;

    public Optional<EnrichedTrainDepartureDto> adapt(TrainArrivalDepartureDto departure, String date) {
        if (departure == null) {
            return Optional.empty();
        }
        List<String> errors = new ArrayList<>();

        return Optional.of(EnrichedTrainDepartureDto.builder()
                .departureTimestamp(utils.getTimestamp(date, departure.time(), errors))
                .departureDelay(utils.getDelay(departure.timeLabel(), errors))
                .platform(utils.getTrainPlatform(departure.platform(), errors))
                .toStation(departure.otherStation())
                .train(departure.train())
                .mainStations(utils.getDirection(departure.mainStations(), errors))
                .stopDuration(utils.getStopDuration(departure.stopLabel(), errors))
                .build());
    }
}