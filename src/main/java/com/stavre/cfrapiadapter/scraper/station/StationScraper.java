package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StationScraper {

    private final StationTrainArrivalDepartureScraper arrivalDepartureScraper;
    private final ScraperUtils utils;

    public List<TrainArrivalDepartureDto> scrapeArrivals(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var tables = pageBody.getElementsByAttributeValue("class", "list-group");
        if (tables.isEmpty()) {
            return List.of();
        }
        return arrivalDepartureScraper.scrapeTrainArrivalsDepartures(tables.getLast());
    }

    public List<TrainArrivalDepartureDto> scrapeDepartures(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var tables = pageBody.getElementsByAttributeValue("class", "list-group");
        if (tables.isEmpty()) {
            return List.of();
        }
        return arrivalDepartureScraper.scrapeTrainArrivalsDepartures(tables.getFirst());
    }
}
