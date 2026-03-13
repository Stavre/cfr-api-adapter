package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.StationTrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository repository;
    private final StationTrainAdapter adapter;


    public List<EnrichedStationTrainDto> getDepartures(String stationName, String date) {
        return repository.getDepartures(stationName, date).stream()
                .map(departure -> adapter.adapt(departure, date))
                .toList();
    }

    public List<EnrichedStationTrainDto> getArrivals(String stationName, String date) {
        return repository.getArrivals(stationName, date).stream()
                .map(departure -> adapter.adapt(departure, date))
                .toList();
    }

    public List<EnrichedStationTrainDto> getDelayedTrains(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .filter(arrival -> !arrival.delay().equals(Duration.ofMinutes(0)))
                .toList();
    }

    public Duration getTotalDelay(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .map(EnrichedStationTrainDto::delay)
                .filter(delay -> !delay.equals(Duration.ofMinutes(0)))
                .reduce(Duration.ofMinutes(0), Duration::plus);
    }
}
