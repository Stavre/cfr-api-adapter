package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.StationTrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository repository;
    private final StationTrainAdapter adapter;

    public List<EnrichedStationTrainDto> getStationTrains(String stationName, String date) {
        List<Optional<EnrichedTrainArrivalDto>> arrivals = repository.getArrivals(stationName, date);
        List<Optional<EnrichedTrainDepartureDto>> departures = repository.getDepartures(stationName, date);

        return createPairs(arrivals, departures).stream()
                .map(pair -> adapter.adapt(pair.getKey(), pair.getValue()))
                .filter(it -> it.isPresent())
                .map(it -> it.get())
                .toList();
    }

    private List<Map.Entry<Optional<EnrichedTrainArrivalDto>, Optional<EnrichedTrainDepartureDto>>> createPairs(
            List<Optional<EnrichedTrainArrivalDto>> arrivals,
            List<Optional<EnrichedTrainDepartureDto>> departures
    ) {
        List<Optional<EnrichedTrainArrivalDto>> arrivalsList = new ArrayList<>(arrivals);
        List<Optional<EnrichedTrainDepartureDto>> departuresList = new ArrayList<>(departures);

        List<Map.Entry<Optional<EnrichedTrainArrivalDto>, Optional<EnrichedTrainDepartureDto>>> pairs = new ArrayList<>();

        for (var arrival : arrivalsList) {
            Optional<EnrichedTrainDepartureDto> matchingDeparture = departuresList.stream()
                    .filter(d ->
                            d.isPresent() && d.get().train().equals(arrival.get().train()))
                    .findFirst()
                    .orElse(Optional.empty());

            pairs.add(Map.entry(arrival, matchingDeparture));
            departuresList.remove(matchingDeparture);
        }

        if (departuresList.isEmpty()) {
            return pairs;
        }

        departuresList.forEach(d -> pairs.add(Map.entry(Optional.empty(), d)));

        return pairs;
    }

    public List<EnrichedStationTrainDto> getDelayedTrains(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .filter(arrival -> !arrival.departureDelay().equals(Duration.ofMinutes(0)))
                .toList();
    }

    public Duration getTotalDelay(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .map(EnrichedStationTrainDto::departureDelay)
                .filter(delay -> !delay.equals(Duration.ofMinutes(0)))
                .reduce(Duration.ofMinutes(0), Duration::plus);
    }
}
