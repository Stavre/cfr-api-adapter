package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.request.RequestDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.dto.train.TrainDto;
import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;
import com.stavre.cfrapiadapter.dto.train.TrainStopDto;

import com.stavre.cfrapiadapter.utils.ScraperUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrainScraper {

    private final VerificationTokensScraper verificationTokensScraper = new VerificationTokensScraper();
    private final ScraperUtils utils = new ScraperUtils();

    public RequestTrainTimeTableDto scrapeRequestTrainTimeTableDetails(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();

        String date = body.getElementById("Date").attribute("value").getValue();
        String trainRunningNumber = body.getElementById("TrainRunningNumber").attribute("value").getValue();
        String selectedBranchCode = body.getElementById("SelectedBranchCode").attribute("value").getValue();

        RequestDto verificationTokensDto = verificationTokensScraper.scrapeVerificationTokens(htmlPage);

        return new RequestTrainTimeTableDto(date, trainRunningNumber, selectedBranchCode, verificationTokensDto);
    }

    public TrainDto scrapeTrain(String html) {
        Optional<TrainMetadataDto> metadataDto = scrapeTrainMetadata(html);
        Map<String, List<Optional<TrainStopDto>>> branchStops = scrapeTrainBranches(html);
        return new TrainDto(metadataDto, branchStops);
    }

    public Optional<TrainMetadataDto> scrapeTrainMetadata(String htmlPage) {
        try {
            Element body = Jsoup.parse(htmlPage).body();
            Element e = body.getElementsByClass("jumbotron p-3 mb-3").getFirst().child(0).child(0);//.child(0);
            String operator = e.child(1).text().replace("Operat de", "").trim();

            String category = e.child(0).child(0).text().trim();
            String number = e.child(0).child(1).text().trim();

            return Optional.of(new TrainMetadataDto(number, category, operator));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Map<String, List<Optional<TrainStopDto>>> scrapeTrainBranches(String html) {
        Element body = Jsoup.parse(html).body();

        Elements timeTables = getTimeTable(body);
        if (timeTables.size() == 1) {
            return Map.of("Main train", scrapeTrainTimeTable(timeTables.getFirst()));
        }

        Map<String, List<Optional<TrainStopDto>>> res = new HashMap<>();

        List<String> branches = getBranches(html);
        for (int i = 0; i < timeTables.size(); i++) {
            res.put(branches.get(i), extractTrainStopsFromTimeTable(timeTables.get(i)));
        }

        return res;

    }

    public List<String> getBranches(String html) {
        Element body = Jsoup.parse(html).body();
        Elements e = body.getElementsByClass("jumbotron p-3 mb-3").getFirst().child(1).getElementsByClass("m-1 flex-grow-1");//.child(0);
        return e.stream().map(el -> el.text()).toList();
    }

    public List<Optional<TrainStopDto>> scrapeTrainTimeTable(Element timeTable) {
//        Element body = Jsoup.parse(htmlPage).body();
//        var timeTable = getTimeTable(body);
//        scrapeTrainMetadata(htmlPage);
        return extractTrainStopsFromTimeTable(timeTable);
    }

    private Elements getTimeTable(Element page) {
        return page.getElementsByAttributeValue("class", "list-group");
    }

    private List<Optional<TrainStopDto>> extractTrainStopsFromTimeTable(Element html) {
        return getTimeTableRows(html).stream()
                .map(this::extractTrainStop)
                .toList();
    }

    private Elements getTimeTableRows(Element page) {
        return page.select("ul.list-group > li.list-group-item");
    }

    private Optional<TrainStopDto> extractTrainStop(Element row) {
        try {
            String departureTime = getDepartureTime(row);
            String departureTimeLabel = getDepartureTimeLabel(row);
            String arrivalTime = getArrivalTime(row);
            String arrivalTimeLabel = getArrivalTimeLabel(row);

            Element innerRow = getInnerRow(row);
            String stationName = getStationName(innerRow);
            List<String> stationLabels = getStationLabel(row);
            String km = getKm(innerRow);
            String stopDuration = getStopDuration(innerRow);
            String platform = getPlatform(row);

            return Optional.of(new TrainStopDto(arrivalTime, arrivalTimeLabel, departureTime, departureTimeLabel, stationName, stationLabels, km, stopDuration, platform));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getDepartureTime(Element row) {
        Elements leftTime = row.child(0).child(2).getElementsByAttributeValue("class", "text-1-3rem text-right");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private String getDepartureTimeLabel(Element row) {
        Elements leftTime = row.child(0).child(2).select(".text-0-8rem");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private String getArrivalTime(Element row) {
        Elements rightTime = row.child(0).child(0).getElementsByAttributeValue("class", "text-1-3rem");
        if (rightTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> rightTime.getFirst().text().trim());
    }

    private String getArrivalTimeLabel(Element row) {
        Elements leftTime = row.child(0).child(0).select(".text-0-8rem");

        if (leftTime == null) {
            return "";
        }
        return utils.getOrBlank(() -> leftTime.getFirst().text().trim());
    }

    private Element getInnerRow(Element row) {
        return row.selectFirst(".w-100 > .row");
    }

    private String getStationName(Element innerRow) {
        Element stationLink = innerRow.selectFirst(".col-md-5 a");
        return utils.getOrBlank(() -> stationLink.text().trim());
    }

    public List<String> getStationLabel(Element row) {
        Element divWithValues = row.child(0).child(1).child(0).child(0).child(1);

        if (divWithValues == null) {
            return List.of();
        }
        Elements values = divWithValues.getElementsByAttributeValue("class", "text-0-8rem");
        return values.stream().map(Element::text).toList();
    }


    private String getKm(Element innerRow) {
        Element kmEl = innerRow.selectFirst(".col-md-2");
        return utils.getOrBlank(() -> kmEl.text().trim());
    }

    private String getStopDuration(Element innerRow) {
        Element durationEl = innerRow.selectFirst(".col-md-3");
        return utils.getOrBlank(() -> durationEl.text().trim());
    }

    private String getPlatform(Element innerRow) {
//        System.out.println("---------------------");
//        System.out.println(innerRow);
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

