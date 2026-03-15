package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StationTrainDeparturesScraper {
    private final StationTrainScraper commonScraper;
    private final StationTrainMetadataScraper trainMetadataScraper;

    public List<TrainArrivalDepartureDto> scrapeTrainDepartures(Element departureTable) {
        return commonScraper.scrapeTableRows(departureTable)
                .parallelStream()
                .map(this::scrapeDepartureTrain)
                .toList();
    }

    public TrainArrivalDepartureDto scrapeDepartureTrain(Element row) {
        String departureTime = commonScraper.scrapeTime(row);
        String departureTimeLabel = commonScraper.scrapeDelayLabel(row);
        String platform = commonScraper.scrapePlatform(row);
        String destinationName = commonScraper.scrapeStationName(row);
        TrainMetadataDto train = trainMetadataScraper.scrapeTrainMetadata(row);
        String mainStations = commonScraper.scrapeMainStations(row);
        String stopDuration = commonScraper.scrapeStopDuration(row);

        return new TrainArrivalDepartureDto(
                departureTime, departureTimeLabel,
                platform, destinationName,
                train, mainStations, stopDuration);
    }
}
