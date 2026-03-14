package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationTrainArrivalsScraper {
    private final StationTrainScraper commonScraper;
    private final StationTrainMetadataScraper trainMetadataScraper;

    public List<Optional<TrainArrivalDto>> scrapeTrainArrivals(Element arrivalTable) {
        return commonScraper.scrapeTableRows(arrivalTable).stream()
                .map(this::scrapeArrivalTrain)
                .toList();
    }

    public Optional<TrainArrivalDto> scrapeArrivalTrain(Element row) {
        String arrivalTime = commonScraper.scrapeTime(row);
        String arrivalTimeLabel = commonScraper.scrapeDelayLabel(row);
        String platform = commonScraper.scrapePlatform(row);
        String originName = commonScraper.scrapeStationName(row);
        TrainMetadataDto train = trainMetadataScraper.scrapeTrainMetadata(row);
        String mainStations = commonScraper.scrapeMainStations(row);
        String stopDuration = commonScraper.scrapeStopDuration(row);

        return Optional.of(
                new TrainArrivalDto(
                        arrivalTime, arrivalTimeLabel,
                        platform, originName,
                        train, mainStations, stopDuration)
        );
    }
}
