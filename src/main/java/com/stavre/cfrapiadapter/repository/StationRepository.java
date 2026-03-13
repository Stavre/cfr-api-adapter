package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.scraper.StationTrainDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.proxy.TrainStationProxy;
import com.stavre.cfrapiadapter.scraper.station.StationRequestScraper;
import com.stavre.cfrapiadapter.scraper.station.StationScraper;
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
    private final StationRequestScraper stationRequestScraper;
    private final StationScraper stationScraper;

    public List<Optional<StationTrainDto>> getDepartures(String stationName, String date) {
        try {
            String response = proxy.getStationTokenPage(stationName, date);
            RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

            String secondResult = proxy.getStationTrains(request);
            return stationScraper.scrapeDepartures(secondResult);
        } catch (RuntimeException e) {
            log.error("Could not extract departures for station %s, date %s".formatted(stationName, date));
            return List.of();
        }
    }

    public List<Optional<StationTrainDto>> getArrivals(String stationName, String date) {
        try {
            String response = proxy.getStationTokenPage(stationName, date);
            RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

            String secondResult = proxy.getStationTrains(request);

            return stationScraper.scrapeArrivals(secondResult);
        } catch (RuntimeException e) {
            log.error("Could not extract arrivals for station %s, date %s".formatted(stationName, date));
            return List.of();
        }
    }
}
