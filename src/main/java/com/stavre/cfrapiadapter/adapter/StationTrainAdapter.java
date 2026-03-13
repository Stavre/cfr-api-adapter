package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.enriched.StationTrainType;
import com.stavre.cfrapiadapter.dto.scraper.StationTrainDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class StationTrainAdapter {

    private final TrainMetadataAdapter trainMetadataAdapter = new TrainMetadataAdapter();
    private final AdapterUtils utils = new AdapterUtils();

    public EnrichedStationTrainDto adapt(Optional<StationTrainDto> trainArrivalDtoOpt, String date) {
        if (trainArrivalDtoOpt.isEmpty()) {
            return new EnrichedStationTrainDto(StationTrainType.ARRIVAL, List.of("Could not scrap train from CFR's station page"));
        }

        StationTrainDto trainArrivalDto = trainArrivalDtoOpt.get();
        List<String> errors = new ArrayList<>();

        LocalDateTime departure = utils.getArrivalTimestamp(date, trainArrivalDto.time(), errors);
        Duration departureDelay = utils.getDelay(trainArrivalDto.timeLabel(), errors);

        String platform = utils.getTrainPlatform(trainArrivalDto.platform(), errors);
        String destination = getOrigin(trainArrivalDto.secondStation(), errors);

        EnrichedTrainMetadataDto train = trainMetadataAdapter.adapt(Optional.of(trainArrivalDto.train()));
        List<String> direction = getDirection(trainArrivalDto.mainStations(), errors);
        Duration stopDuration = getStopDuration(trainArrivalDto.stopDuration(), errors);
        LocalDateTime stopStartsAt = getStopEndsAt(date, trainArrivalDto.stopDuration(), errors);

        return new EnrichedStationTrainDto(StationTrainType.ARRIVAL, departure,
                departureDelay, platform, destination, train, direction, stopDuration, stopStartsAt, errors);
    }

    private LocalDateTime getStopEndsAt(String date, String duration, List<String> errors) {
        if (duration.contains("necunoscută")) {
            return null;
        }


        String extractedTime = duration.replace(")", "").split("la")[1].trim();
        Optional<LocalDate> dateOpt = utils.convertDate(date);
        Optional<LocalTime> timeOpt = utils.convertTime(extractedTime);

        if (dateOpt.isEmpty()) {
            errors.add("Could not parse date %s into date object".formatted(date));
            return null;
        }

        if (timeOpt.isEmpty()) {
            errors.add("Could not extract time object from label %s".formatted(duration));
            return null;
        }

        return dateOpt.get().atTime(timeOpt.get());

    }

    private Duration getStopDuration(String duration, List<String> errors) {
        try {
            if (duration.contains("necunoscută")) {
                return null;
            }
            int durationAsInt = Integer.parseInt(duration.split(" ")[0]);
            return Duration.ofMinutes(durationAsInt);
        } catch (Exception e) {
            errors.add("Could not convert label %s into duration".formatted(duration));
            return null;
        }
    }

    private List<String> getDirection(String mainStations, List<String> errors) {
        if (mainStations.isBlank()) {
            errors.add("No main stations found");
            return null;
        }

        return Arrays.stream(mainStations.split("-"))
                .map(String::trim)
                .toList();
    }

    private String getOrigin(String originName, List<String> errors) {
        if (originName.isBlank()) {
            errors.add("Origin name is blank");
            return null;
        }

        return originName.trim();
    }
}
