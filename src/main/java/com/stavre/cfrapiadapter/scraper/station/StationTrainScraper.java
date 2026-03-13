package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.train.StationTrainDto;
import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationTrainScraper {
//    TODO: Find a better name for this class

    public List<Optional<StationTrainDto>> scrapeStationTrains(Element table) {
        return scrapeTableRows(table).stream()
                .map(this::scrapeStationTrain)
                .toList();
    }


    private Optional<StationTrainDto> scrapeStationTrain(Element row) {
        try {

            String departureTime = scrapeTime(row);
            String departureTimeLabel = scrapeDelayLabel(row);
            String platform = scrapePlatform(row);
            String destinationName = scrapeStationName(row);
            TrainMetadataDto train = scrapeTrainMetadata(row);
            String mainStations = scrapeMainStations(row);
            String stopDuration = scrapeStopDuration(row);

            return Optional.of(
                    new StationTrainDto(
                            departureTime, departureTimeLabel,
                            platform, destinationName,
                            train, mainStations, stopDuration)
            );

        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private Elements scrapeTableRows(Element table) {
        return table.select("ul.list-group > li.list-group-item");
    }

    private String scrapeMainStations(Element row) {
        try {
            return row.child(0).child(1).child(0).child(1).child(0).child(1).text();
        } catch (Exception e) {
            return "";
        }
    }

    private TrainMetadataDto scrapeTrainMetadata(Element row) {
        String trainCategory = "";
        String trainNumber = "";
        String operator = "";

        Element trainBlock = row.select(".col-md-2 .line-height-1-25").get(1);

        Element catSpan = trainBlock.selectFirst("span[class^=span-train-category]");
        trainCategory = catSpan == null ? "" : catSpan.text().trim();

        Element trainA = trainBlock.selectFirst("a[href*=/Tren/]");
        trainNumber = trainA == null ? "" : trainA.text().trim();

        Element opImg = row.selectFirst("img.img-train-operator");
        operator = opImg == null ? "" : opImg.attr("alt").trim();

        return new TrainMetadataDto(trainNumber, trainCategory, operator);
    }

    private String scrapeStationName(Element row) {
        try {
            Element stationElement = row.selectFirst(".col-md-3 a");
            return stationElement.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String scrapePlatform(Element row) {
        try {
            Element badge = row.selectFirst(".div-stations-train-real-time-badge");
            Element platformEl = badge.selectFirst("div.d-inline-block.ml-3, div.ml-3");
            return platformEl.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String scrapeDelayLabel(Element row) {
        try {
            Element badge = row.selectFirst(".div-stations-train-real-time-badge");
            Element firstInline = badge.selectFirst("div.d-inline-block, div.color-firebrick, div.color-gray");
            return firstInline.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String scrapeStopDuration(Element row) {
        Element details = row.selectFirst(".div-departures-arrivails-details");

        Elements labelCols = details.select(".col-12 .row");
        for (Element _row : labelCols) {
            Element label = _row.selectFirst(".col-sm-3 .color-blue");
            Element value = _row.selectFirst(".col-sm-9");

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

    private String scrapeTime(Element row) {
        try {
            Element depTimeEl = row.selectFirst(".col-md-2 .line-height-1-25 > div:nth-of-type(2)");
            return depTimeEl.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
