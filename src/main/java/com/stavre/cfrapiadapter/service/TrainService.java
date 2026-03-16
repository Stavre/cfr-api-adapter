package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.TrainAdapter;
import com.stavre.cfrapiadapter.adapter.TrainTimestampAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.repository.TrainRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TrainService {

    private final ObjectMapper objectMapper;
    private List<String> trains;
    private final TrainRepository trainRepository;
    private final TrainAdapter trainAdapter;
    private final TrainTimestampAdapter timestampAdapter;

    public EnrichedTrainDto getTrainStops(String trainId, String date) {
        TrainDto scraped = trainRepository.getTrainStops(trainId, date);
        var enrichedTrain = trainAdapter.adapt(scraped, date);
        return timestampAdapter.accountForMultipleDayJourney(enrichedTrain);
    }

    public TrainDelayResponseDto getTrainDelay(EnrichedTrainDto train) {

        var branchStops = train.stops()
                .entrySet().stream()
                .map(s -> Map.entry(s.getKey(), s.getValue().getLast()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new TrainDelayResponseDto(
                train.metadata(),
                branchStops
        );
    }

    public TrainDelayResponseDto getTrainDelay(EnrichedTrainDto train, String stationName) {

        var branchStops = train.stops()
                .entrySet().stream()
                .filter(s -> s.getValue().stream().anyMatch(stop -> stop.station().equals(stationName)))
                .map(
                        s -> Map.entry(s.getKey(), s.getValue()
                                .stream()
                                .filter(st -> st.station().equals(stationName))
                                .findFirst().orElseThrow())
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new TrainDelayResponseDto(
                train.metadata(),
                branchStops
        );
    }

    @PostConstruct
    public void init() throws IOException {
        try (InputStream is = new ClassPathResource("trains.json").getInputStream()) {
            trains = objectMapper.readValue(is, new TypeReference<>() {});
        }
    }

    public List<String> getAllTrains() {
        return trains;
    }
}
