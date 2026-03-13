package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TrainMetadataScraper {

    public Optional<TrainMetadataDto> scrapeMetadata(Element pageBody) {
        try {
            Element containerElement = scrapeContainerElement(pageBody);

            String operator = scrapeTrainOperator(containerElement);
            String category = scrapeTrainCategory(containerElement);
            String number = scrapeTrainNumber(containerElement);

            return Optional.of(new TrainMetadataDto(number, category, operator));
        } catch (Exception e) {
            return Optional.empty();
        }
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
}
