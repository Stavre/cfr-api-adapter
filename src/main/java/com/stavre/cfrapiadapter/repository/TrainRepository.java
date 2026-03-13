package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.train.TrainDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import com.stavre.cfrapiadapter.scraper.train.TrainRequestScraper;
import com.stavre.cfrapiadapter.scraper.train.TrainScraper;
import com.stavre.cfrapiadapter.validator.TrainPageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TrainRepository {

    private final TrainTimeTableProxy proxy;
    private final TrainScraper scraper;
    private final TrainRequestScraper requestScraper;
    private final TrainPageValidator validator = new TrainPageValidator();

    public TrainDto getTrainStops(String trainId, String date) {
        String tokenPage = proxy.getTrainTimeTable(trainId, date);
        RequestTrainTimeTableDto request = requestScraper.scrapeRequestDetails(tokenPage);
        String trainStopsPage = proxy.getTrainTimeTablePost(request);
        validator.validate(trainStopsPage, trainId, date);

        return scraper.scrapeTrain(trainStopsPage);
    }
}
