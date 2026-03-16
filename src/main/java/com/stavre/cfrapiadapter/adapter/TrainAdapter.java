package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainAdapter {

    private final TrainStopAdapter trainStopAdapter;

    public EnrichedTrainDto adapt(TrainDto dto, String date) {
        return new EnrichedTrainDto(
                dto.metadata(),
                adaptStops(dto.branchStops(), date)
        );
    }

    private Map<TrainBranchDto, List<EnrichedTrainStopDto>> adaptStops(
            Map<TrainBranchDto, List<Optional<TrainStopDto>>> stops,
            String date
    ) {
        return stops.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), enrichStops(e.getValue(), date)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<EnrichedTrainStopDto> enrichStops(List<Optional<TrainStopDto>> stops, String date) {
        if (stops == null || stops.isEmpty()) {
            return List.of();
        }

        return enrichStop(stops, date);
    }

    private List<EnrichedTrainStopDto> enrichStop(List<Optional<TrainStopDto>> stops, String date) {
        return stops.stream()
                .map(stop -> trainStopAdapter.adapt(stop, date))
                .toList();
    }
}
