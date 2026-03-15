package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StationTrainArrivalDepartureScraper {
    private final StationTrainScraper commonScraper;
    private final StationTrainMetadataScraper trainMetadataScraper;

    public List<TrainArrivalDepartureDto> scrapeTrainArrivalsDepartures(Element arrivalTable) {
        return commonScraper.scrapeTableRows(arrivalTable)
                .parallelStream()
                .map(this::scrapeTrainArrivalDeparture)
                .toList();
    }

    public TrainArrivalDepartureDto scrapeTrainArrivalDeparture(Element row) {
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
