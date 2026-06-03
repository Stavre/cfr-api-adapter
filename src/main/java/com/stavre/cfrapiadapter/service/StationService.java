package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.StationTrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.response.StationDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationService {

    private final ObjectMapper objectMapper;
    private List<StationDto> stations;

    private final StationRepository repository;
    private final StationTrainAdapter adapter;

    public List<EnrichedStationTrainDto> getStationTrains(String stationName, String date) {
        List<Optional<EnrichedTrainArrivalDepartureDto>> arrivals = repository.getArrivals(stationName, date);

        List<Optional<EnrichedTrainArrivalDepartureDto>> departures = repository.getDepartures(stationName, date);

        return createPairs(arrivals, departures).stream()
                .map(pair -> adapter.adapt(pair.getKey(), pair.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private List<Map.Entry<Optional<EnrichedTrainArrivalDepartureDto>, Optional<EnrichedTrainArrivalDepartureDto>>>
        createPairs(
            List<Optional<EnrichedTrainArrivalDepartureDto>> arrivals,
            List<Optional<EnrichedTrainArrivalDepartureDto>> departures
    ) {
        List<Optional<EnrichedTrainArrivalDepartureDto>> arrivalsList = new ArrayList<>(arrivals);
        List<Optional<EnrichedTrainArrivalDepartureDto>> departuresList = new ArrayList<>(departures);

        List<Map.Entry<Optional<EnrichedTrainArrivalDepartureDto>, Optional<EnrichedTrainArrivalDepartureDto>>> pairs
                = new ArrayList<>();

        for (var arrival : arrivalsList) {
            Optional<EnrichedTrainArrivalDepartureDto> matchingDeparture = departuresList.stream()
                    .filter(Optional::isPresent)
                    .filter(d -> arrival.isPresent() && d.get().train().equals(arrival.get().train()))
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

    public List<EnrichedStationTrainDto> getDelayedArrivals(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .filter(
                        arrival ->
                                arrival.arrivalDelay() != null
                                        && !arrival.arrivalDelay().equals(Duration.ofMinutes(0)))
                .toList();
    }

    public List<EnrichedStationTrainDto> getDelayedDepartures(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .filter(arrival ->
                        arrival.departureDelay() != null
                                && !arrival.departureDelay().equals(Duration.ofMinutes(0)))
                .toList();
    }

    public Duration getTotalArrivalsDelay(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .map(EnrichedStationTrainDto::arrivalDelay)
                .reduce(Duration.ofMinutes(0), Duration::plus);
    }

    public Duration getTotalDeparturesDelay(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains
                .parallelStream()
                .map(EnrichedStationTrainDto::departureDelay)
                .reduce(Duration.ofMinutes(0), Duration::plus);
    }

    @PostConstruct
    public void init() throws IOException {
        try (InputStream is = new ClassPathResource("stations.json").getInputStream()) {
            stations = objectMapper.readValue(is, new TypeReference<>() {
            });
        }
    }

    public List<StationDto> getAllStations() {
        return stations;
    }
}
