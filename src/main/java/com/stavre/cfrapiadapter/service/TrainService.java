package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.dto.TrainStopDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import com.stavre.cfrapiadapter.repository.StationRepository;
import com.stavre.cfrapiadapter.scraper.TrainTimeTableScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrainService {

    private final StationRepository repository;

    private final TrainTimeTableProxy proxy;
    private final TrainTimeTableScraper scraper = new TrainTimeTableScraper();

    public List<TrainStopDto> getTrainStops(String trainId, String date) {
        String tokenPage = proxy.getTrainTimeTable(trainId, date);
        RequestTrainTimeTableDto request = scraper.scrapeRequestTrainTimeTableDetails(tokenPage);
        String trainStopsPage = proxy.getTrainTimeTablePost(request);

        if (trainStopsPage.contains("nu circulă în data de")) {
            return List.of();
        }

        return scraper.scrapeTrainTimeTable(trainStopsPage);
    }


    public List<String> getAllTrains(String date) {
        List<String> stations = new ArrayList<>(1700);
        List<String> trainNumbers = new ArrayList<>();

        helper("Bucuresti-Nord", date, stations, trainNumbers);
        return trainNumbers;
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
        return getTrainStops(trainNumber, date).stream()
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
