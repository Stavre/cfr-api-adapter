package com.stavre.cfrapiadapter.scraper.train;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TrainBranchScraper {

    public Elements scrapeTrainTimeTables(Element page) {
        return page.getElementsByAttributeValue("class", "list-group");
    }

    public List<String> scrapeBranches(Element pageBody) {
        Elements branches = scrapeBranchElements(pageBody);
        if (branches.isEmpty()) {
            return List.of("Main train");
        }
        return branches.stream().map(el -> el.text()).toList();
    }

//    TODO: Add Pojo class for Branch

    private Elements scrapeBranchElements(Element pageBody) {
        return pageBody
                .getElementsByClass("jumbotron p-3 mb-3")
                .getFirst()
                .child(1)
                .getElementsByClass("m-1 flex-grow-1");
    }
}