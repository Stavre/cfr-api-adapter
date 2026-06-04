package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.adapter.EnrichedTrainArrivalDepartureAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.proxy.TrainStationProxy;
import com.stavre.cfrapiadapter.scraper.station.StationRequestScraper;
import com.stavre.cfrapiadapter.scraper.station.StationScraper;
import com.stavre.cfrapiadapter.validator.PageValidator;
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
    private final PageValidator validator;

    private final EnrichedTrainArrivalDepartureAdapter enrichedTrainArrivalDepartureAdapter;

    public List<Optional<EnrichedTrainArrivalDepartureDto>> getArrivals(String stationName, String date) {
        String response = proxy.getStationTokenPage(stationName, date);
        validator.validate(response);
        RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

        String secondResult = proxy.getStationTrains(request);

        return scraper.scrapeArrivals(secondResult)
                .parallelStream()
                .map(arrival -> enrichedTrainArrivalDepartureAdapter.adapt(arrival, date))
                .toList();
    }

    public List<Optional<EnrichedTrainArrivalDepartureDto>> getDepartures(String stationName, String date) {
        String response = proxy.getStationTokenPage(stationName, date);
        validator.validate(response);
        RequestStationTrainsDto request = stationRequestScraper.scrapeRequestDetails(response);

        String secondResult = proxy.getStationTrains(request);
        return scraper.scrapeDepartures(secondResult)
                .parallelStream()
                .map(departure -> enrichedTrainArrivalDepartureAdapter.adapt(departure, date))
                .toList();
    }
}
