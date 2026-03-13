package com.stavre.cfrapiadapter.scraper.station;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class StationTableScraper {


    public Element scrapeDeparturesTable(Element pageBody) {
        return pageBody.getElementsByAttributeValue("class", "list-group").getFirst();
    }

    public Element scrapeArrivalsTable(Element pageBody) {
        return pageBody.getElementsByAttributeValue("class", "list-group").getLast();
    }
}
