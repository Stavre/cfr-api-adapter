package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class StationTrainMetadataScraper {

    public TrainMetadataDto scrapeTrainMetadata(Element row) {
        Element trainContainer = scrapeTrainContainer(row);

        String trainCategory = scrapeCategory(trainContainer);
        String trainNumber = scrapeNumber(trainContainer);
        String trainOperator = scrapeOperator(trainContainer);
        String trainId = getTrainId(trainCategory, trainNumber);

        return new TrainMetadataDto(trainId, trainNumber, trainCategory, trainOperator);
    }

    private Element scrapeTrainContainer(Element row) {
        return row.getElementsByClass("row").getFirst();
    }

    private String scrapeCategory(Element trainContainer) {
        Element catSpan = trainContainer.selectFirst("span[class^=span-train-category]");
        return catSpan == null ? "" : catSpan.text().trim();
    }

    private String scrapeNumber(Element trainContainer) {
        Element trainA = trainContainer.selectFirst("a[href*=/Tren/]");
        return trainA == null ? "" : trainA.text().trim();
    }

    private String scrapeOperator(Element trainContainer) {
        Element opImg = trainContainer.selectFirst("img.img-train-operator");
        return opImg == null ? "" : opImg.attr("alt").trim();
    }

    private String getTrainId(String trainCategory, String trainNumber) {
        return "%s %s".formatted(trainCategory, trainNumber);
    }
}