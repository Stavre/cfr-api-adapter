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
        var arrivalsTable = scrapeArrivalsTable(pageBody);
        return arrivalDepartureScraper.scrapeTrainArrivalsDepartures(arrivalsTable);
    }

    public List<TrainArrivalDepartureDto> scrapeDepartures(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);
        var departuresTable = scrapeDeparturesTable(pageBody);
        return arrivalDepartureScraper.scrapeTrainArrivalsDepartures(departuresTable);
    }

    private Element scrapeDeparturesTable(Element pageBody) {
        return pageBody.getElementsByAttributeValue("class", "list-group").getFirst();
    }

    private Element scrapeArrivalsTable(Element pageBody) {
        return pageBody.getElementsByAttributeValue("class", "list-group").getLast();
    }
}
