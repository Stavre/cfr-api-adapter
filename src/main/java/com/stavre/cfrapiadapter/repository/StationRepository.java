package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.train.TrainArrivalDto;
import com.stavre.cfrapiadapter.dto.train.TrainDepartureDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.proxy.TrainStationProxy;
import com.stavre.cfrapiadapter.scraper.StationArrivalsScraper;
import com.stavre.cfrapiadapter.scraper.StationDeparturesScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class StationRepository {
    private final TrainStationProxy proxy;
    private final StationDeparturesScraper departuresScraper = new StationDeparturesScraper();
    private final StationArrivalsScraper arrivalsScraper = new StationArrivalsScraper();

    public List<Optional<TrainDepartureDto>> getDepartures(String stationName, String date) {
        try {
            String response = proxy.getStationTrains(stationName, date);
            RequestStationTrainsDto request = departuresScraper.scrapeRequestStationTrainsDetails(response);
            String secondResult = proxy.getStationTrainsPost(request);

            return departuresScraper.scrapeStationDepartures(secondResult);
        } catch (RuntimeException e) {
            log.error("Could not extract departures for station %s, date %s".formatted(stationName, date));
            return List.of();
        }
    }

    public List<Optional<TrainArrivalDto>> getArrivals(String stationName, String date) {
        try {
            String response = proxy.getStationTrains(stationName, date);
            RequestStationTrainsDto request = arrivalsScraper.scrapeRequestStationTrainsDetails(response);
            String secondResult = proxy.getStationTrainsPost(request);

            return arrivalsScraper.scrapeStationArrivals(secondResult);
        } catch (RuntimeException e) {
            log.error("Could not extract arrivals for station %s, date %s".formatted(stationName, date));
            return List.of();
        }
    }
}
