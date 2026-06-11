package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.response.StationDto;
import com.stavre.cfrapiadapter.proxy.ItinerariesProxy;
import com.stavre.cfrapiadapter.scraper.ItinerariesScraper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ItinerariesRepository {

    private final ItinerariesProxy proxy;
    private final ItinerariesScraper scraper;
    private List<String> trainNumbers;
    private List<StationDto> stations;

    @PostConstruct
    public void init() {
        String html = proxy.getItinerariesPage();
        trainNumbers = scraper.scrapeTrainNumbers(html);
        stations = scraper.scrapeStations(html);
    }

    public List<String> getTrainNumbers() {
        return trainNumbers;
    }

    public List<StationDto> getStations() {
        return stations;
    }
}
