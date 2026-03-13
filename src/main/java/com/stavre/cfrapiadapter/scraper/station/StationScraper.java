package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.train.StationTrainDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationScraper {

    private final StationTrainScraper stationTrainScraper;
    private final StationTableScraper stationTableScraper;
    private final ScraperUtils utils;


    public List<Optional<StationTrainDto>> scrapeArrivals(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var arrivalsTable = stationTableScraper.scrapeArrivalsTable(pageBody);
        return stationTrainScraper.scrapeStationTrains(arrivalsTable);
    }

    public List<Optional<StationTrainDto>> scrapeDepartures(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var departuresTable = stationTableScraper.scrapeDeparturesTable(pageBody);
        return stationTrainScraper.scrapeStationTrains(departuresTable);
    }
}
