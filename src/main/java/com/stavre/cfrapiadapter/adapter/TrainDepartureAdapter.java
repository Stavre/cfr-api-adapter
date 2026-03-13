package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.scraper.StationTrainDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TrainDepartureAdapter {

    private final TrainMetadataAdapter trainMetadataAdapter = new TrainMetadataAdapter();
    private final AdapterUtils utils = new AdapterUtils();

    public EnrichedTrainDepartureDto adapt(Optional<StationTrainDto> trainDepartureDtoOpt, String date) {
        if (trainDepartureDtoOpt.isEmpty()) {
            return new EnrichedTrainDepartureDto(List.of("Could not scrap train departure from CFR page"));
        }

        StationTrainDto trainDepartureDto = trainDepartureDtoOpt.get();
        List<String> errors = new ArrayList<>();

        LocalDateTime departure = utils.getDepartureTimestamp(date, trainDepartureDto.time(), errors);
        Duration departureDelay = utils.getDelay(trainDepartureDto.timeLabel(), errors);

        String platform = utils.getTrainPlatform(trainDepartureDto.platform(), errors);
        String destination = getDestination(trainDepartureDto.secondStation(), errors);

        EnrichedTrainMetadataDto train = trainMetadataAdapter.adapt(Optional.of(trainDepartureDto.train()));
        List<String> direction = getDirection(trainDepartureDto.mainStations(), errors);
        Duration stopDuration = getStopDuration(trainDepartureDto.stopDuration(), errors);
        LocalDateTime stopStartsAt = getStopStartsAt(date, trainDepartureDto.stopDuration(), errors);

        return new EnrichedTrainDepartureDto(departure,
                departureDelay, platform, destination, train, direction, stopDuration, stopStartsAt, errors);

    }

    private LocalDateTime getStopStartsAt(String date, String duration, List<String> errors) {
        if (duration.contains("necunoscută")) {
            return null;
        }
        String extractedTime = duration.replace(")", "").split("cu")[1].trim();
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

    private String getDestination(String destinationName, List<String> errors) {
        if (destinationName.isBlank()) {
            errors.add("Destination name is blank");
            return null;
        }

        return destinationName.trim();
    }
}
