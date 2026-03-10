package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.*;
import com.stavre.cfrapiadapter.dto.request.RequestDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

@Slf4j
public class StationTrainsScraper {
    private final VerificationTokensScraper verificationTokensScraper = new VerificationTokensScraper();

    public RequestStationTrainsDto scrapeRequestStationTrainsDetails(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();
        String date = body.getElementById("Date").attribute("value").getValue();
        String trainRunningNumber = body.getElementById("StationName").attribute("value").getValue();

        RequestDto verificationTokensDto = verificationTokensScraper.scrapeVerificationTokens(htmlPage);
        return new RequestStationTrainsDto(date, trainRunningNumber, verificationTokensDto);
    }

    public List<StationTrainDepartureDto> scrapeStationDepartures(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();
        var departuresTable = body.getElementsByAttributeValue("class", "list-group").getFirst();
        return extractDeparturesFromTable(departuresTable);
    }

    private List<StationTrainDepartureDto> extractDeparturesFromTable(Element table) {
        return getDepartureTableRows(table).stream()
                .map(this::extractDeparture)
                .toList();
    }

    private Elements getDepartureTableRows(Element table) {
        return table.select("ul.list-group > li.list-group-item");
    }

    private StationTrainDepartureDto extractDeparture(Element row) {
        try {
            String departureTime = getDepartureTime(row);
            String destinationName = getDestinationName(row);
            String destinationHref = getDestinationHref(row);

            TrainDto train = getTrain(row);

            String stopDuration = getStopDuration(row);
            String delayLabel = getDelayLabel(row);
            String platform = getPlatform(row);

            return new StationTrainDepartureDto(departureTime, destinationName, destinationHref, train, stopDuration, delayLabel, platform);

        } catch (RuntimeException e) {
            throw new RuntimeException("Could not process row.", e);
        }
    }

    private String getDepartureTime(Element row) {
        try {
            Element depTimeEl = row.selectFirst(".col-md-2 .line-height-1-25 > div:nth-of-type(2)");
            return depTimeEl.text().trim();
        } catch (NullPointerException e) {
            return "";
        }

    }

    private String getDestinationName(Element row) {
        try {
        Element destA = row.selectFirst(".col-md-3 a");
        return destA.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String getDestinationHref(Element row) {
        try {
        Element destA = row.selectFirst(".col-md-3 a");
        return destA.attr("href").trim();
    } catch (NullPointerException e) {
        return "";
    }
    }

    private TrainDto getTrain(Element row) {
        String trainCategory = "";
        String trainNumber = "";
        String operator = "";

        Element trainBlock = row.select(".col-md-2 .line-height-1-25").get(1);

        Element catSpan = trainBlock.selectFirst("span[class^=span-train-category]");
        trainCategory = catSpan == null ? "" : catSpan.text().trim();

        Element trainA = trainBlock.selectFirst("a[href*=/Tren/]");
        trainNumber = trainA  == null ? "" : trainA.text().trim();

        Element opImg = row.selectFirst("img.img-train-operator");
        operator = opImg  == null ? "" : opImg.attr("alt").trim();

        return new TrainDto(trainNumber, trainCategory, operator);
    }

    private String getStopDuration(Element row) {
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

    private String getDelayLabel(Element row) {
        try {
        Element badge = row.selectFirst(".div-stations-train-real-time-badge");
        Element firstInline = badge.selectFirst("div.d-inline-block, div.color-firebrick, div.color-gray");
        return firstInline.text().trim();
    } catch (NullPointerException e) {
        return "";
        }
    }

    private String getPlatform(Element row) {
    try {
        Element badge = row.selectFirst(".div-stations-train-real-time-badge");
        Element platformEl = badge.selectFirst("div.d-inline-block.ml-3, div.ml-3");
        return platformEl.text().trim();
    } catch (NullPointerException e) {
        return "";
        }
    }
}
