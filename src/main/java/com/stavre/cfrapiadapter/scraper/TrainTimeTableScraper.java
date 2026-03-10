package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.request.RequestDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.dto.TrainStopDto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class TrainTimeTableScraper {

    private final VerificationTokensScraper verificationTokensScraper = new VerificationTokensScraper();

    public RequestTrainTimeTableDto scrapeRequestTrainTimeTableDetails(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();

        String date = body.getElementById("Date").attribute("value").getValue();
        String trainRunningNumber = body.getElementById("TrainRunningNumber").attribute("value").getValue();
        String selectedBranchCode = body.getElementById("SelectedBranchCode").attribute("value").getValue();

        RequestDto verificationTokensDto = verificationTokensScraper.scrapeVerificationTokens(htmlPage);

        return new RequestTrainTimeTableDto(date, trainRunningNumber, selectedBranchCode, verificationTokensDto);
    }

    public List<TrainStopDto> scrapeTrainTimeTable(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();
        var timeTable = body.getElementsByAttributeValue("class", "list-group").getFirst();
        return extractTrainStopsFromTimeTable(timeTable);
    }

    private List<TrainStopDto> extractTrainStopsFromTimeTable(Element html) {
        return getTimeTableRows(html).stream()
                .map(this::extractTrainStop)
                .toList();
    }

    private Elements getTimeTableRows(Element page) {
        return page.select("ul.list-group > li.list-group-item");
    }

    private TrainStopDto extractTrainStop(Element row) {
        String departureTime = getDepartureTime(row);
        String arrivalTime = getArrivalTime(row);

        Element innerRow = getInnerRow(row);
        String stationName = getStationName(innerRow);
        String stationHref = getStationHref(innerRow);
        String km = getKm(innerRow);
        String stopDuration = getStopDuration(innerRow);
        String platform = getPlatform(innerRow);
        String statusLabel = getStatusLabel(row);

        return new TrainStopDto(departureTime, arrivalTime, stationName, stationHref, km, stopDuration, platform, statusLabel);
    }

    private String getDepartureTime(Element row) {
        Element leftTime = row.selectFirst(".col-3 .text-1-3rem");
        return leftTime.text().trim();
    }

    private String getArrivalTime(Element row) {
        Elements rightTimes = row.select(".col-3 .text-1-3rem");
        return rightTimes.last().text().trim();
    }

    private String getStatusLabel(Element row) {
        Element statusEl = row.selectFirst(".text-0-8rem");
        return statusEl == null ? "" : statusEl.text().trim();
    }

    private Element getInnerRow(Element row) {
        return row.selectFirst(".w-100 > .row");
    }

    private String getStationName(Element innerRow) {
        Element stationLink = innerRow.selectFirst(".col-md-5 a");
        return stationLink == null ? "" : stationLink.text().trim();
    }

    private String getStationHref(Element innerRow) {
        Element stationLink = innerRow.selectFirst(".col-md-5 a");
        return stationLink == null ? "" : stationLink.attr("href").trim();
    }

    private String getKm(Element innerRow) {
        Element kmEl = innerRow.selectFirst(".col-md-2");
        return kmEl == null ? "" : kmEl.text().trim();
    }

    private String getStopDuration(Element innerRow) {
        Element durationEl = innerRow.selectFirst(".col-md-3");
        return durationEl == null ? "" : durationEl.text().trim();
    }

    private String getPlatform(Element innerRow) {
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
