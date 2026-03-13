package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.TrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.repository.StationRepository;
import com.stavre.cfrapiadapter.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrainService {

    private final StationRepository repository;
    private final TrainRepository trainRepository;
    private final TrainAdapter trainAdapter = new TrainAdapter();


    public EnrichedTrainDto getTrainStops(String trainId, String date) {
        TrainDto scraped = trainRepository.getTrainStops(trainId, date);
        return trainAdapter.adapt(scraped, date);
    }

//
//    public List<String> getAllTrains(String date) {
//        List<String> stations = new ArrayList<>(1700);
//        List<String> trainNumbers = new ArrayList<>();
//
//        helper("Bucuresti-Nord", date, stations, trainNumbers);
//        return trainNumbers;
//    }
//
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
//
//    private List<String> helperTrains(String stationName, String date) {
//        return repository.getDepartures(stationName, date).stream()
//                .map(departure -> departure.get().train().trainNumber())
//                .toList();
//    }
//
//    private List<String> helperStations(String trainNumber, String date) {
//        return getTrainStops(trainNumber, date).stream()
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
