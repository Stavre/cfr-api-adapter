package com.stavre.cfrapiadapter.repository;

import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import com.stavre.cfrapiadapter.scraper.train.TrainRequestScraper;
import com.stavre.cfrapiadapter.scraper.train.TrainScraper;
import com.stavre.cfrapiadapter.validator.PageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TrainRepository {

    private final TrainTimeTableProxy proxy;
    private final TrainScraper scraper;
    private final TrainRequestScraper requestScraper;
    private final PageValidator validator;

    public TrainDto getTrainStops(String trainId, String date) {
        String tokenPage = proxy.getTrainTokenPage(trainId, date);
        validator.validate(tokenPage);

        RequestTrainTimeTableDto request = requestScraper.scrapeRequestDetails(tokenPage);

        String trainStopsPage = proxy.getTrainTimeTable(request);
        validator.validate(trainStopsPage);

        return scraper.scrapeTrain(trainStopsPage);
    }
}
