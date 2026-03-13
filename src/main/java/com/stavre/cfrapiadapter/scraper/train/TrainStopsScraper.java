package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.train.TrainStopDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TrainStopsScraper {

    private final ScraperUtils utils;


    public List<Optional<TrainStopDto>> scrapeTrainStops(Element timeTableElement) {
        return scrapeTrainStopRowElements(timeTableElement).stream()
                .map(this::scrapeTrainStop)
                .toList();
    }

    private Elements scrapeTrainStopRowElements(Element page) {
        return page.select("ul.list-group > li.list-group-item");
    }

    private Optional<TrainStopDto> scrapeTrainStop(Element row) {
        try {
            String departureTime = scrapeDepartureTime(row);
            String departureTimeLabel = scrapeDepartureTimeLabel(row);
            String arrivalTime = scrapeArrivalTime(row);
            String arrivalTimeLabel = scrapeArrivalTimeLabel(row);

            Element innerRow = scrapeInnerRow(row);
            String stationName = scrapeStationName(innerRow);
            List<String> stationLabels = scrapeStationLabel(row);
            String km = scrapeKm(innerRow);
            String stopDuration = scrapeStopDuration(innerRow);
            String platform = scrapePlatform(row);

            return Optional.of(new TrainStopDto(arrivalTime, arrivalTimeLabel, departureTime, departureTimeLabel, stationName, stationLabels, km, stopDuration, platform));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String scrapeDepartureTime(Element row) {
        Elements leftTime = row.child(0).child(2).getElementsByAttributeValue("class", "text-1-3rem text-right");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private String scrapeDepartureTimeLabel(Element row) {
        Elements leftTime = row.child(0).child(2).select(".text-0-8rem");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private String scrapeArrivalTime(Element row) {
        Elements rightTime = row.child(0).child(0).getElementsByAttributeValue("class", "text-1-3rem");
        if (rightTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> rightTime.getFirst().text().trim());
    }

    private String scrapeArrivalTimeLabel(Element row) {
        Elements leftTime = row.child(0).child(0).select(".text-0-8rem");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private Element scrapeInnerRow(Element row) {
        return row.selectFirst(".w-100 > .row");
    }

    private String scrapeStationName(Element innerRow) {
        Element stationLink = innerRow.selectFirst(".col-md-5 a");
        return utils.getOrBlank(() -> stationLink.text().trim());
    }

    public List<String> scrapeStationLabel(Element row) {
        Element divWithValues = row.child(0).child(1).child(0).child(0).child(1);

        if (divWithValues == null) {
            return List.of();
        }
        Elements values = divWithValues.getElementsByAttributeValue("class", "text-0-8rem");
        return values.stream().map(Element::text).toList();
    }


    private String scrapeKm(Element innerRow) {
        Element kmEl = innerRow.selectFirst(".col-md-2");
        return utils.getOrBlank(() -> kmEl.text().trim());
    }

    private String scrapeStopDuration(Element innerRow) {
        Element durationEl = innerRow.selectFirst(".col-md-3");
        return utils.getOrBlank(() -> durationEl.text().trim());
    }

    private String scrapePlatform(Element innerRow) {
        Elements allCols = innerRow.select(".col-md-2, .col-md-3, .col-md-5");
        for (Element c : allCols) {
            String t = c.text().trim();
            if (t.toLowerCase().contains("linia")) {
                return t;
            }
        }

        return "";
    }
}
