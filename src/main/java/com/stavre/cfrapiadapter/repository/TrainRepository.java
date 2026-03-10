package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.TrainStopDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import com.stavre.cfrapiadapter.scraper.TrainTimeTableScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TrainRepository {

    private final TrainTimeTableProxy proxy;
    private final TrainTimeTableScraper scraper = new TrainTimeTableScraper();

    public List<TrainStopDto> getTrainStops(String trainId, String date) {
        String tokenPage = proxy.getTrainTimeTable(trainId, date);
        RequestTrainTimeTableDto request = scraper.scrapeRequestTrainTimeTableDetails(tokenPage);
        String trainStopsPage = proxy.getTrainTimeTablePost(request);

        return scraper.scrapeTrainTimeTable(trainStopsPage);
    }
}
