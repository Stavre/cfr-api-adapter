package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainAdapter {
    private final TrainMetadataAdapter trainMetadataAdapter = new TrainMetadataAdapter();
    private final TrainStopAdapter trainStopAdapter;

    public EnrichedTrainDto adapt(TrainDto dto, String date) {
        return new EnrichedTrainDto(
                trainMetadataAdapter.adapt(dto.metadata().get()),
                getStops(dto.branchStops(), date)
        );
    }

    private Map<TrainBranchDto, List<EnrichedTrainStopDto>> getStops(
            Map<TrainBranchDto, List<Optional<TrainStopDto>>> stops,
            String date
    ) {
        return stops.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), getEnrichedStops(e.getValue(), date)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<EnrichedTrainStopDto> getEnrichedStops(List<Optional<TrainStopDto>> stops, String date) {
        List<EnrichedTrainStopDto> enrichedStops = stops
                .stream()
                .map(stop -> trainStopAdapter.adapt(stop, date))
                .toList();

        List<EnrichedTrainStopDto> list = new ArrayList<>();

        list.add(enrichedStops.getFirst());
        for (int i = 1; i < enrichedStops.size(); i++) {
            EnrichedTrainStopDto newStop = enrichedStops.get(i);
            EnrichedTrainStopDto lastStop = list.get(i - 1);
            EnrichedTrainStopDto currentStop = enrichedStops.get(i);


            if (currentStop.arrival() != null && lastStop.arrival() != null) {
                if (currentStop.arrival().toLocalTime().isBefore(lastStop.arrival().toLocalTime())) {
                    LocalDateTime newTimestamp = lastStop.arrival().toLocalDate().plusDays(1)
                            .atTime(currentStop.arrival().toLocalTime());
                    newStop = newStop.withArrival(newTimestamp);
                } else {
                    LocalDateTime newTimestamp = lastStop.arrival().toLocalDate()
                            .atTime(currentStop.arrival().toLocalTime());
                    newStop = newStop.withArrival(newTimestamp);
                }
            }

            if (currentStop.departure() != null && lastStop.departure() != null) {
                if (currentStop.departure().toLocalTime().isBefore(lastStop.departure().toLocalTime())) {
                    LocalDateTime newTimestamp = lastStop.departure().toLocalDate()
                            .plusDays(1).atTime(currentStop.departure().toLocalTime());
                    newStop = newStop.withDeparture(newTimestamp);
                } else {
                    LocalDateTime newTimestamp = lastStop.departure().toLocalDate()
                            .atTime(currentStop.departure().toLocalTime());
                    newStop = newStop.withDeparture(newTimestamp);
                }
            }

            list.add(newStop);
        }
        return list;
    }
}
