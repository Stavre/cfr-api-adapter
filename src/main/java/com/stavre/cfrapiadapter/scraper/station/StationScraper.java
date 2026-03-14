package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDepartureDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationScraper {

    private final StationTrainArrivalsScraper arrivalsScraper;
    private final StationTrainDeparturesScraper departuresScraper;
    private final StationTableScraper stationTableScraper;
    private final ScraperUtils utils;

    public List<Optional<TrainArrivalDto>> scrapeArrivals(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var arrivalsTable = stationTableScraper.scrapeArrivalsTable(pageBody);
        return arrivalsScraper.scrapeTrainArrivals(arrivalsTable);
    }

    public List<Optional<TrainDepartureDto>> scrapeDepartures(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var departuresTable = stationTableScraper.scrapeDeparturesTable(pageBody);
        return departuresScraper.scrapeTrainDepartures(departuresTable);
    }
}
