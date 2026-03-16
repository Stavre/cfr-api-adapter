package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TrainTimestampAdapter {

    public EnrichedTrainDto accountForMultipleDayJourney(EnrichedTrainDto train) {
        var adjustedTrainStops = train.stops()
                .entrySet()
                .stream()
                .map(set -> Map.entry(set.getKey(), normalizeTimestamps(set.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return EnrichedTrainDto.builder()
                .metadata(train.metadata())
                .stops(adjustedTrainStops).build();
    }

    private List<EnrichedTrainStopDto> normalizeTimestamps(List<EnrichedTrainStopDto> enrichedStops) {
        if (enrichedStops == null || enrichedStops.isEmpty()) {
            return List.of();
        }

        List<EnrichedTrainStopDto> result = new ArrayList<>(enrichedStops.size());
        result.add(enrichedStops.getFirst());

        for (int i = 1; i < enrichedStops.size(); i++) {
            EnrichedTrainStopDto previous = result.getLast();
            EnrichedTrainStopDto current = enrichedStops.get(i);

            current = adjustArrival(previous, current);
            current = adjustDeparture(previous, current);

            result.add(current);
        }

        return result;
    }

    private EnrichedTrainStopDto adjustArrival(EnrichedTrainStopDto previous, EnrichedTrainStopDto current) {
        LocalDateTime prevArrival = previous == null ? null : previous.arrival();
        LocalDateTime currArrival = current == null ? null : current.arrival();

        LocalDateTime adjusted = adjustTimestamp(prevArrival, currArrival);
        if (adjusted != null && !Objects.equals(adjusted, currArrival)) {
            return current.withArrival(adjusted);
        }
        return current;
    }

    private EnrichedTrainStopDto adjustDeparture(EnrichedTrainStopDto previous, EnrichedTrainStopDto current) {
        LocalDateTime prevDeparture = previous == null ? null : previous.departure();
        LocalDateTime currDeparture = current == null ? null : current.departure();

        LocalDateTime adjusted = adjustTimestamp(prevDeparture, currDeparture);
        if (adjusted != null && !Objects.equals(adjusted, currDeparture)) {
            return current.withDeparture(adjusted);
        }
        return current;
    }

    private LocalDateTime adjustTimestamp(LocalDateTime previous, LocalDateTime current) {
        if (current == null) {
            return null;
        }
        if (previous == null) {
            return current;
        }

        LocalTime prevTime = previous.toLocalTime();
        LocalTime currTime = current.toLocalTime();
        LocalDate baseDate = previous.toLocalDate();

        return currTime.isBefore(prevTime)
                ? baseDate.plusDays(1).atTime(currTime)
                : baseDate.atTime(currTime);

    }
}
