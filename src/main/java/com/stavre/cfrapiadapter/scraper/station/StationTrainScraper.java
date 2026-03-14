package com.stavre.cfrapiadapter.scraper.station;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationTrainScraper {

    public List<Element> scrapeTableRows(Element table) {
        return table.select("ul.list-group > li.list-group-item");
    }

    public String scrapeMainStations(Element row) {
        try {
            return row.child(0).child(1).child(0).child(1).child(0).child(1).text();
        } catch (Exception e) {
            return "";
        }
    }

    public String scrapeStationName(Element row) {
        return Optional.ofNullable(row.selectFirst(".col-md-3 a"))
                .map(Element::text)
                .map(String::trim)
                .orElse("");
    }

    public String scrapePlatform(Element row) {
        return Optional.ofNullable(row.selectFirst(".div-stations-train-real-time-badge"))
                .map(badge -> badge.selectFirst("div.d-inline-block.ml-3, div.ml-3"))
                .map(Element::text)
                .map(String::trim)
                .orElse("");
    }

    public String scrapeDelayLabel(Element row) {
        if (row == null) {
            return "";
        }

        return Optional.ofNullable(row.selectFirst(".div-stations-train-real-time-badge"))
                .map(badge -> badge.selectFirst("div.d-inline-block, div.color-firebrick, div.color-gray"))
                .map(Element::text)
                .map(String::trim)
                .orElse("");
    }

    public String scrapeStopDuration(Element container) {
        Element details = container.selectFirst(".div-departures-arrivails-details");

        List<Element> labelCols = details.select(".col-12 .row");
        for (Element row : labelCols) {
            Element label = row.selectFirst(".col-sm-3 .color-blue");
            Element value = row.selectFirst(".col-sm-9");

            if (label == null || value == null) {
                continue;
            }

            String labelText = label.text().trim();
            if (labelText.startsWith("Staționare")) {
                return value.text().trim();
            }
        }

        return "";
    }

    public String scrapeTime(Element row) {
        return Optional.ofNullable(row.selectFirst(".col-md-2 .line-height-1-25 > div:nth-of-type(2)"))
                .map(Element::text)
                .map(String::trim)
                .orElse("");
    }
}