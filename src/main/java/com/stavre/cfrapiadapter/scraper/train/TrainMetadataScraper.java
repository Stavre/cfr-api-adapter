package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainMetadataScraper {

    public TrainMetadataDto scrapeMetadata(Element pageBody) {

        Element containerElement = scrapeContainerElement(pageBody);

        String operator = scrapeTrainOperator(containerElement);
        String category = scrapeTrainCategory(containerElement);
        String number = scrapeTrainNumber(containerElement);
        String id = getTrainId(category, number);

        return new TrainMetadataDto(id, number, category, operator);
    }

    private Element scrapeContainerElement(Element pageBody) {
        return pageBody
                .getElementsByClass("jumbotron p-3 mb-3")
                .getFirst()
                .child(0)
                .child(0);
    }

    private String scrapeTrainOperator(Element containerElement) {
        return containerElement
                .child(1)
                .text()
                .replace("Operat de", "")
                .trim();
    }

    private String scrapeTrainCategory(Element containerElement) {
        return containerElement
                .child(0)
                .child(0)
                .text()
                .trim();
    }

    private String scrapeTrainNumber(Element containerElement) {
        return containerElement
                .child(0)
                .child(1)
                .text()
                .trim();
    }

    private String getTrainId(String trainCategory, String trainNumber) {
        return "%s %s".formatted(trainCategory, trainNumber);
    }
}
