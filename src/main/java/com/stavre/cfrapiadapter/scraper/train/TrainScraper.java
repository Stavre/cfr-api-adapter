package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.train.TrainDto;
import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;
import com.stavre.cfrapiadapter.dto.train.TrainStopDto;

import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        Optional<TrainMetadataDto> metadataDto = trainMetadataScraper.scrapeMetadata(pageBody);
        Map<String, List<Optional<TrainStopDto>>> branchStops = scrapeTrainBranches(pageBody);
        return new TrainDto(metadataDto, branchStops);
    }

    public Map<String, List<Optional<TrainStopDto>>> scrapeTrainBranches(Element pageBody) {

        Elements timeTables = trainBranchScraper.scrapeTrainTimeTables(pageBody);
        List<String> branches = trainBranchScraper.scrapeBranches(pageBody);
        List<List<Optional<TrainStopDto>>> stops = timeTables.stream()
                .map(trainStopsScraper::scrapeTrainStops)
                .toList();

        int size = timeTables.size();

        return IntStream.range(0, size)
                .boxed().collect(Collectors.toMap(branches::get, stops::get));
    }
}

