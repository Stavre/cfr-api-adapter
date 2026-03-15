package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StationTrainArrivalsScraper {
    private final StationTrainScraper commonScraper;
    private final StationTrainMetadataScraper trainMetadataScraper;

    public List<TrainArrivalDepartureDto> scrapeTrainArrivals(Element arrivalTable) {
        return commonScraper.scrapeTableRows(arrivalTable)
                .parallelStream()
                .map(this::scrapeArrivalTrain)
                .toList();
    }

    public TrainArrivalDepartureDto scrapeArrivalTrain(Element row) {
        String arrivalTime = commonScraper.scrapeTime(row);
        String arrivalTimeLabel = commonScraper.scrapeDelayLabel(row);
        String platform = commonScraper.scrapePlatform(row);
        String originName = commonScraper.scrapeStationName(row);
        TrainMetadataDto train = trainMetadataScraper.scrapeTrainMetadata(row);
        String mainStations = commonScraper.scrapeMainStations(row);
        String stopDuration = commonScraper.scrapeStopDuration(row);

        return new TrainArrivalDepartureDto(
                arrivalTime, arrivalTimeLabel,
                platform, originName,
                train, mainStations, stopDuration);
    }
}
