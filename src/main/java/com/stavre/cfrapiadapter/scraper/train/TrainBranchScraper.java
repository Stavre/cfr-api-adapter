package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TrainBranchScraper {

    public Elements scrapeTrainTimeTables(Element page) {
        return page.getElementsByAttributeValue("class", "list-group");
    }

    public List<TrainBranchDto> scrapeBranches(Element pageBody) {
        Elements branches = scrapeBranchElements(pageBody);
        if (branches.isEmpty()) {
            return List.of(new TrainBranchDto("Main branch", null, null));
        }

        return branches.stream().map(this::scrapeBranch).toList();
    }

    private Elements scrapeBranchElements(Element pageBody) {
        return pageBody
                .getElementsByClass("jumbotron p-3 mb-3")
                .getFirst()
                .child(1)
                .getElementsByClass("m-1 flex-grow-1");
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