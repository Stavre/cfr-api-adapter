package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import com.stavre.cfrapiadapter.validator.TrainPageValidator;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Nodes;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class TrainScraper {

    private final ScraperUtils utils;
    private final TrainMetadataScraper trainMetadataScraper;
    private final TrainBranchScraper trainBranchScraper;
    private final TrainStopsScraper trainStopsScraper;

    public TrainDto scrapeTrain(String html) {
        Element pageBody = utils.scrapePageBody(html);

        TrainMetadataDto metadataDto = trainMetadataScraper.scrapeMetadata(pageBody);
        Map<TrainBranchDto, List<Optional<TrainStopDto>>> branchStops = scrapeTrainBranches(pageBody);
        return new TrainDto(metadataDto, branchStops);
    }

    public Map<TrainBranchDto, List<Optional<TrainStopDto>>> scrapeTrainBranches(Element pageBody) {

        List<Element> timeTables = trainBranchScraper.scrapeTrainTimeTables(pageBody);
        List<TrainBranchDto> branches = trainBranchScraper.scrapeBranches(pageBody);
        List<List<Optional<TrainStopDto>>> stops = timeTables.stream()
                .map(trainStopsScraper::scrapeTrainStops)
                .toList();

        int size = timeTables.size();

        return IntStream.range(0, size)
                .boxed().collect(Collectors.toMap(branches::get, stops::get));
    }
}

