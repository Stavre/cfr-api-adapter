package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.adapter.EnrichedTrainArrivalAdapter;
import com.stavre.cfrapiadapter.adapter.EnrichedTrainDepartureAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
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
    private final StationScraper scraper;

    private final EnrichedTrainArrivalAdapter arrivalAdapter;
    private final EnrichedTrainDepartureAdapter departureAdapter;

    public List<Optional<EnrichedTrainArrivalDto>> getArrivals(String stationName, String date) {
        String response = proxy.getStationTokenPage(stationName, date);
        RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

        String secondResult = proxy.getStationTrains(request);

        return scraper.scrapeArrivals(secondResult)
                .parallelStream()
                .map(arrival -> arrivalAdapter.adapt(arrival, date))
                .toList();
    }

    public List<Optional<EnrichedTrainDepartureDto>> getDepartures(String stationName, String date) {
        String response = proxy.getStationTokenPage(stationName, date);
        RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

        String secondResult = proxy.getStationTrains(request);
        return scraper.scrapeDepartures(secondResult)
                .parallelStream()
                .map(departure -> departureAdapter.adapt(departure, date))
                .toList();
    }
}
