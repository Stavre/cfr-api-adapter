package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDepartureDto;
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
    private final TrainMetadataAdapter trainMetadataAdapter;

    public Optional<EnrichedTrainDepartureDto> adapt(Optional<TrainDepartureDto> departure, String date) {
        if (departure.isEmpty()) {
            return Optional.empty();
        }
        List<String> errors = new ArrayList<>();
        TrainDepartureDto trainDeparture = departure.get();

        EnrichedTrainDepartureDto enrichedDeparture = EnrichedTrainDepartureDto.builder()
                .departureTimestamp(utils.getTimestamp(date, trainDeparture.departureTime(), errors))
                .departureDelay(utils.getDelay(trainDeparture.departureTimeLabel(), errors))
                .platform(utils.getTrainPlatform(trainDeparture.platform(), errors))
                .toStation(trainDeparture.destinationName())
                .train(trainMetadataAdapter.adapt(trainDeparture.train()))
                .mainStations(utils.getDirection(trainDeparture.mainStations(), errors))
                .stopDuration(utils.getStopDuration(trainDeparture.stopLabel(), errors))
                .build();

        return Optional.of(enrichedDeparture);
    }
}