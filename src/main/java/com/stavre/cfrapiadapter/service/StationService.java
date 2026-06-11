package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.StationTrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.response.StationDto;
import com.stavre.cfrapiadapter.repository.ItinerariesRepository;
import com.stavre.cfrapiadapter.repository.StationRepository;
import jakarta.annotation.PostConstruct;
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

    private List<StationDto> stations;

    private final StationRepository repository;
    private final StationTrainAdapter adapter;
    private final ItinerariesRepository itinerariesRepository;

    public List<EnrichedStationTrainDto> getStationTrains(String stationName, String date) {
        String pageContent = repository.getPageContent(stationName, date);

        List<Optional<EnrichedTrainArrivalDepartureDto>> arrivals =
                repository.getArrivalsFromPageContent(pageContent, date);

        List<Optional<EnrichedTrainArrivalDepartureDto>> departures =
                repository.getDeparturesFromPageContent(pageContent, date);

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

    public List<EnrichedStationTrainDto> getArrivals(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains.parallelStream()
                .filter(train -> train.arrival() != null)
                .toList();
    }

    public List<EnrichedStationTrainDto> getDepartures(List<EnrichedStationTrainDto> stationTrains) {
        return stationTrains.parallelStream()
                .filter(train -> train.departure() != null)
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
    public void init() {
        stations = itinerariesRepository.getStations();
    }

    public List<StationDto> getAllStations() {
        return stations;
    }
}
