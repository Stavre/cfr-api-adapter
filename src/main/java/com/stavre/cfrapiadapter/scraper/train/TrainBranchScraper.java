package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TrainBranchScraper {

    private static final int MIN_CHILDREN_FOR_BRANCHES = 2;

    public List<Element> scrapeTrainTimeTables(Element page) {
        return page.getElementsByAttributeValue("class", "list-group");
    }

    public List<TrainBranchDto> scrapeBranches(Element pageBody) {
        List<Element> branches = scrapeBranchElements(pageBody);
        if (branches.isEmpty()) {
            return List.of(new TrainBranchDto("Main branch",
                    scrapeFirstStopStation(pageBody),
                    scrapeLastStopStation(pageBody)));
        }

        return branches.stream().map(this::scrapeBranch).toList();
    }

    private String scrapeFirstStopStation(Element pageBody) {
        Element timetable = pageBody.selectFirst("ul.list-group");
        if (timetable == null) {
            return null;
        }
        Element firstRow = timetable.selectFirst("li.list-group-item");
        return scrapeStationFromStopRow(firstRow);
    }

    private String scrapeLastStopStation(Element pageBody) {
        Element timetable = pageBody.selectFirst("ul.list-group");
        if (timetable == null) {
            return null;
        }
        Element lastRow = timetable.select("li.list-group-item").last();
        return scrapeStationFromStopRow(lastRow);
    }

    private String scrapeStationFromStopRow(Element row) {
        if (row == null) {
            return null;
        }
        Element stationLink = row.selectFirst(".col-md-5 a");
        if (stationLink == null) {
            return null;
        }
        return stationLink.text().trim();
    }

    private List<Element> scrapeBranchElements(Element pageBody) {
        Element jumbotron = pageBody
                .getElementsByClass("jumbotron p-3 mb-3")
                .getFirst();
        if (jumbotron.childrenSize() < MIN_CHILDREN_FOR_BRANCHES) {
            return List.of();
        }
        return jumbotron.child(1).getElementsByClass("m-1 flex-grow-1");
    }

    private TrainBranchDto scrapeBranch(Element el) {
        String branchName = scrapeBranchName(el);
        String originStation = scrapeOriginStation(el);
        String destinationStation = scrapeDestinationStation(el);

        return new TrainBranchDto(branchName, originStation, destinationStation);
    }

    private String scrapeBranchName(Element el) {
        return el.select("h3").first().ownText().trim();
    }

    private String scrapeOriginStation(Element el) {
        return el.select("span.text-1-3rem").textNodes().getFirst().text().trim();
    }

    private String scrapeDestinationStation(Element el) {
        return el.select("span.text-1-3rem").textNodes().getLast().text().trim();
    }
}