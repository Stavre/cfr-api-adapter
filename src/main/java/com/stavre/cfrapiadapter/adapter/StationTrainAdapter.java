package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationTrainAdapter {

    /**
     * Merge arrival and departure DTOs into a single EnrichedStationTrainDto.
     * Rules:
     * - If both inputs are empty -> return Optional.empty()
     * - If one input is empty -> set the fields coming from that side to null and add an error
     * - Common fields (stopDuration, train, platform, direction) are set only when both DTOs are present
     *   and their values are equal. If they differ -> set the common field to null and add an error.
     *
     * @param arrivalOpt   optional arrival DTO
     * @param departureOpt optional departure DTO
     * @return optional merged EnrichedStationTrainDto
     */
    public Optional<EnrichedStationTrainDto> adapt(
            Optional<EnrichedTrainArrivalDepartureDto> arrivalOpt,
            Optional<EnrichedTrainArrivalDepartureDto> departureOpt) {

        if (arrivalOpt.isEmpty() && departureOpt.isEmpty()) {
            return Optional.empty();
        }

        List<String> errors = new ArrayList<>();

        EnrichedTrainArrivalDepartureDto arrival = arrivalOpt.orElse(null);
        EnrichedTrainArrivalDepartureDto departure = departureOpt.orElse(null);

        String fromStation = arrivalOpt.map(EnrichedTrainArrivalDepartureDto::otherStation).orElse(null);
        LocalDateTime arrivalTimestamp = arrivalOpt.map(EnrichedTrainArrivalDepartureDto::timestamp).orElse(null);
        Duration arrivalDelay = arrivalOpt.map(EnrichedTrainArrivalDepartureDto::delay).orElse(null);

        String toStation = departureOpt.map(EnrichedTrainArrivalDepartureDto::otherStation).orElse(null);
        LocalDateTime departureTimestamp = departureOpt.map(EnrichedTrainArrivalDepartureDto::timestamp).orElse(null);
        Duration departureDelay = departureOpt.map(EnrichedTrainArrivalDepartureDto::delay).orElse(null);

        Duration stopDuration = null;
        TrainMetadataDto train = null;
        String platform = null;
        List<String> direction = null;

        if (arrival != null && departure != null) {
            // stopDuration
            if (Objects.equals(arrival.stopDuration(), departure.stopDuration())) {
                stopDuration = arrival.stopDuration();
            } else {
                errors.add(String.format("Mismatched stopDuration: arrival=%s, departure=%s",
                        arrival.stopDuration(), departure.stopDuration()));
            }

            // train (EnrichedTrainMetadataDto is a record so equals() works)
            if (Objects.equals(arrival.train(), departure.train())) {
                train = arrival.train();
            } else {
                errors.add(String.format("Mismatched train metadata: arrival=%s, departure=%s",
                        arrival.train(), departure.train()));
            }

            // platform
            if (Objects.equals(arrival.platform(), departure.platform())) {
                platform = arrival.platform();
            } else {
                errors.add(String.format("Mismatched platform: arrival=%s, departure=%s",
                        arrival.platform(), departure.platform()));
            }

            // direction (mainStations)
            if (Objects.equals(arrival.mainStations(), departure.mainStations())) {
                direction = arrival.mainStations();
            } else {
                errors.add(String.format("Mismatched direction (mainStations): arrival=%s, departure=%s",
                        arrival.mainStations(), departure.mainStations()));
            }
        } else if (arrival != null) {
            stopDuration = arrival.stopDuration();
            train = arrival.train();
            platform = arrival.platform();
            direction = arrival.mainStations();
            errors.add("Missing departure information");
        } else if (departure != null) {
            stopDuration = departure.stopDuration();
            train = departure.train();
            platform = departure.platform();
            direction = departure.mainStations();
            errors.add("Missing arrival information");
        }

        EnrichedStationTrainDto result = EnrichedStationTrainDto.builder()
                .fromStation(fromStation)
                .arrival(arrivalTimestamp)
                .arrivalDelay(arrivalDelay)
                .toStation(toStation)
                .departure(departureTimestamp)
                .departureDelay(departureDelay)
                .stopDuration(stopDuration)
                .train(train)
                .platform(platform)
                .direction(direction)
                .errors(errors)
                .build();

        return Optional.of(result);
    }
}
