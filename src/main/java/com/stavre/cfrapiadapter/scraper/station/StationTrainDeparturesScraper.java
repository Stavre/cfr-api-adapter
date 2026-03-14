package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StationTrainDeparturesScraper {
    private final StationTrainScraper commonScraper;
    private final StationTrainMetadataScraper trainMetadataScraper;

    public List<Optional<TrainDepartureDto>> scrapeTrainDepartures(Element departureTable) {
        return commonScraper.scrapeTableRows(departureTable).stream()
                .map(this::scrapeDepartureTrain)
                .toList();
    }

    public Optional<TrainDepartureDto> scrapeDepartureTrain(Element row) {
        String departureTime = commonScraper.scrapeTime(row);
        String departureTimeLabel = commonScraper.scrapeDelayLabel(row);
        String platform = commonScraper.scrapePlatform(row);
        String destinationName = commonScraper.scrapeStationName(row);
        TrainMetadataDto train = trainMetadataScraper.scrapeTrainMetadata(row);
        String mainStations = commonScraper.scrapeMainStations(row);
        String stopDuration = commonScraper.scrapeStopDuration(row);

        return Optional.of(
                new TrainDepartureDto(
                        departureTime, departureTimeLabel,
                        platform, destinationName,
                        train, mainStations, stopDuration)
        );
    }
}
