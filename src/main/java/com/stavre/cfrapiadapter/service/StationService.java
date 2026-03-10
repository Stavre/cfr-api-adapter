package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.dto.TrainStopDto;
import com.stavre.cfrapiadapter.dto.StationTrainDepartureDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository repository;
    private final TrainService trainService;

    private final List<String> startStations = List.of("Bucuresti-Nord", "Constanța", "Craiova", "Arad", "Iași", "Brașov");

    public List<StationTrainDepartureDto> getDepartures(String stationName, String date) {
        return repository.getDepartures(stationName, date);
    }

    public List<String> getAllStations(String date) {
        List<String> stations = new ArrayList<>(1700);
        List<String> trainNumbers = new ArrayList<>();

        helper("Bucuresti-Nord", date, stations, trainNumbers);
        return stations;
    }

    private void helper(String stationName, String date, List<String> stations, List<String> trains) {
        System.out.println("stationName: %s".formatted(stationName));
        List<String> trainNumbers = helperTrains(stationName, date);
        List<String> unvisitedTrainNumbers = findMissing(trainNumbers, trains);

        trains.addAll(unvisitedTrainNumbers);

        for (String trainNumber : unvisitedTrainNumbers) {
            System.out.println("trainNumber: %s".formatted(trainNumber));
            List<String> trainStations = helperStations(trainNumber, date);
            List<String> unvisitedStations = findMissing(trainStations, stations);

            stations.addAll(unvisitedStations);
            unvisitedStations.forEach(station -> helper(station, date, stations, trains));
        }
    }

    private List<String> helperTrains(String stationName, String date) {
        return repository.getDepartures(stationName, date).stream()
                .map(departure -> departure.train().trainNumber())
                .toList();
    }

    private List<String> helperStations(String trainNumber, String date) {
        return trainService.getTrainStops(trainNumber, date).stream()
                .map(TrainStopDto::stationName)
                .toList();
    }

    private List<String> findMissing(List<String> candidate, List<String> all) {
        List<String> missingStations = new ArrayList<>();
        for (String station : candidate) {
            if (!all.contains(station)) {
                missingStations.add(station);
            }
        }

        return missingStations;
    }
}
