package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.TrainArrivalAdapter;
import com.stavre.cfrapiadapter.adapter.TrainDepartureAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.train.TrainStopDto;
import com.stavre.cfrapiadapter.dto.train.TrainDepartureDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository repository;
    private final TrainService trainService;
    private final TrainDepartureAdapter departureAdapter = new TrainDepartureAdapter();
    private final TrainArrivalAdapter arrivalAdapter = new TrainArrivalAdapter();

    private final List<String> startStations = List.of("Bucuresti-Nord", "Constanța", "Craiova", "Arad", "Iași", "Brașov");

    public List<EnrichedTrainDepartureDto> getDepartures(String stationName, String date) {
        return repository.getDepartures(stationName, date).stream()
                .map(departure -> departureAdapter.adapt(departure, date))
                .toList();
    }

    public List<EnrichedTrainArrivalDto> getArrivals(String stationName, String date) {
        return repository.getArrivals(stationName, date).stream()
                .map(departure -> arrivalAdapter.adapt(departure, date))
                .toList();
    }

//    public List<String> getAllStations(String date) {
//        List<String> stations = new ArrayList<>(1700);
//        List<String> trainNumbers = new ArrayList<>();
//
//        helper("Bucuresti-Nord", date, stations, trainNumbers);
//        return stations;
//    }

//    private void helper(String stationName, String date, List<String> stations, List<String> trains) {
//        System.out.println("stationName: %s".formatted(stationName));
//        List<String> trainNumbers = helperTrains(stationName, date);
//        List<String> unvisitedTrainNumbers = findMissing(trainNumbers, trains);
//
//        trains.addAll(unvisitedTrainNumbers);
//
//        for (String trainNumber : unvisitedTrainNumbers) {
//            System.out.println("trainNumber: %s".formatted(trainNumber));
//            List<String> trainStations = helperStations(trainNumber, date);
//            List<String> unvisitedStations = findMissing(trainStations, stations);
//
//            stations.addAll(unvisitedStations);
//            unvisitedStations.forEach(station -> helper(station, date, stations, trains));
//        }
//    }

    private List<String> helperTrains(String stationName, String date) {
        return repository.getDepartures(stationName, date).stream()
                .map(departure -> departure.get().train().trainNumber())
                .toList();
    }

//    private List<String> helperStations(String trainNumber, String date) {
//        return trainService.getTrainStops(trainNumber, date).stream()
//                .filter(s -> s.isPresent())
//                .map(s -> s.get())
//                .map(TrainStopDto::stationName)
//                .toList();
//    }

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
