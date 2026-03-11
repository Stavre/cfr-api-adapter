package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.request.RequestDto;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.dto.train.TrainArrivalDto;
import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Optional;

@Slf4j
public class StationArrivalsScraper {
    private final VerificationTokensScraper verificationTokensScraper = new VerificationTokensScraper();

    public RequestStationTrainsDto scrapeRequestStationTrainsDetails(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();
        String date = body.getElementById("Date").attribute("value").getValue();
        String trainRunningNumber = body.getElementById("StationName").attribute("value").getValue();

        RequestDto verificationTokensDto = verificationTokensScraper.scrapeVerificationTokens(htmlPage);
        return new RequestStationTrainsDto(date, trainRunningNumber, verificationTokensDto);
    }

    public List<Optional<TrainArrivalDto>> scrapeStationArrivals(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();
        var departuresTable = getArrivalsTable(body);
        return extractArrivalsFromTable(departuresTable);
    }

    private Element getArrivalsTable(Element htmlPage) {
        return htmlPage.getElementsByAttributeValue("class", "list-group").getLast();
    }

    private List<Optional<TrainArrivalDto>> extractArrivalsFromTable(Element table) {
        return getArrivalTableRows(table).stream()
                .map(this::extractArrival)
                .toList();
    }

    private Elements getArrivalTableRows(Element table) {
        return table.select("ul.list-group > li.list-group-item");
    }

    private Optional<TrainArrivalDto> extractArrival(Element row) {
        try {
            String departureTime = getArrivalTime(row);
            String departureTimeLabel = getDelayLabel(row);
            String platform = getPlatform(row);

            String destinationName = getOriginName(row);

            TrainMetadataDto train = getTrain(row);
            String mainStations = getMainStations(row);
            String stopDuration = getStopDuration(row);

            return Optional.of(
                    new TrainArrivalDto(
                            departureTime, departureTimeLabel,
                            platform, destinationName,
                            train, mainStations, stopDuration)
            );

        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private String getMainStations(Element row) {
        try {
            return row.child(0).child(1).child(0).child(1).child(0).child(1).text();
        } catch (Exception e) {
            return "";
        }
    }

    private String getArrivalTime(Element row) {
        try {
            Element depTimeEl = row.selectFirst(".col-md-2 .line-height-1-25 > div:nth-of-type(2)");
            return depTimeEl.text().trim();
        } catch (NullPointerException e) {
            return "";
        }

    }

    private String getOriginName(Element row) {
        try {
            Element destA = row.selectFirst(".col-md-3 a");
            return destA.text().trim();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private TrainMetadataDto getTrain(Element row) {
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
