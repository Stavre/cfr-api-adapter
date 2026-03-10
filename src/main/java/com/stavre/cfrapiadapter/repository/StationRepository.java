package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.StationTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.proxy.TrainStationProxy;
import com.stavre.cfrapiadapter.scraper.StationTrainsScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class StationRepository {
    private final TrainStationProxy proxy;
    private final StationTrainsScraper scraper = new StationTrainsScraper();

    public List<StationTrainDepartureDto> getDepartures(String stationName, String date) {
        try {
            String response = proxy.getStationTrains(stationName, date);
            RequestStationTrainsDto request = scraper.scrapeRequestStationTrainsDetails(response);
            String secondResult = proxy.getStationTrainsPost(request);

            return scraper.scrapeStationDepartures(secondResult);
        } catch (RuntimeException e) {
            log.error("Could not extract departures for station %s, date %s".formatted(stationName, date));
            return List.of();
        }


    }
}
