package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.train.TrainStopDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import com.stavre.cfrapiadapter.scraper.TrainScraper;
import com.stavre.cfrapiadapter.validator.TrainPageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TrainRepository {

    private final TrainTimeTableProxy proxy;
    private final TrainScraper scraper = new TrainScraper();
    private final TrainPageValidator validator = new TrainPageValidator();

    public List<Optional<TrainStopDto>> getTrainStops(String trainId, String date) {
        String tokenPage = proxy.getTrainTimeTable(trainId, date);
        RequestTrainTimeTableDto request = scraper.scrapeRequestTrainTimeTableDetails(tokenPage);
        String trainStopsPage = proxy.getTrainTimeTablePost(request);
        validator.validate(trainStopsPage, trainId, date);

        return scraper.scrapeTrainTimeTable(trainStopsPage);
    }
}
