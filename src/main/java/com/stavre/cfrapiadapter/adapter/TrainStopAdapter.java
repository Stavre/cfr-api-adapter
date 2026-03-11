package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.train.TrainStopDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainStopAdapter {

    public final AdapterUtils utils = new AdapterUtils();

    public EnrichedTrainStopDto adapt(Optional<TrainStopDto> scrapedDtoOpt, String date) {
        if (scrapedDtoOpt.isEmpty()) {
            return new EnrichedTrainStopDto(List.of("Could not scrape this train stop from CFR page."));
        }

        TrainStopDto scrapedDto = scrapedDtoOpt.get();
        List<String> errors = new ArrayList<>();

        LocalDateTime arrivalTimestamp = utils.getArrivalTimestamp(date, scrapedDto.arrivalTime(), errors);
        Duration arrivalDelay = utils.getDelay(scrapedDto.arrivalTimeLabel(), errors);

        LocalDateTime departureTimestamp = utils.getDepartureTimestamp(date, scrapedDto.departureTime(), errors);
        Duration departureDelay = utils.getDelay(scrapedDto.departureTimeLabel(), errors);

        String station = getStation(scrapedDto.stationName(), errors);
        Integer journeyKm = getJourneyKm(scrapedDto.km(), errors);
        Duration stopDuration = getStopDuration(scrapedDto.stopDuration(), errors);
        String platform = utils.getTrainPlatform(scrapedDto.platform(), errors);
        List<String> trainStopMessages = scrapedDto.stationLabels();

        return new EnrichedTrainStopDto(arrivalTimestamp, arrivalDelay,
                departureTimestamp,departureDelay,
                station, journeyKm,
                stopDuration, platform,
                trainStopMessages, errors);
    }

    private Duration getStopDuration(String duration, List<String> errors) {
        String d = duration.split(" ")[0];

        try {
            return Duration.ofMinutes(Integer.parseInt(d));
        } catch (Exception e) {
            errors.add("Stop duration %s could not be converted to Duration".formatted(duration));
            return null;
        }
    }

    private Integer getJourneyKm(String km, List<String> errors) {
        try {

            int journeyKm = Integer.parseInt(km.replace("km", "").trim());
            if (journeyKm < 0) {
                errors.add("Expected number of kilometers to be greater than or equal to 0, instead it was %s".formatted(km));
                return null;
            }

            return journeyKm;
        } catch (Exception e) {
            errors.add("Could not convert %s to number of kilometers".formatted(km));
            return null;
        }
    }

    private String getStation(String stationName, List<String> errors) {
        if (stationName.isBlank()) {
            errors.add("Station name is blank");
            return null;
        }

        return stationName.trim();
    }
}
