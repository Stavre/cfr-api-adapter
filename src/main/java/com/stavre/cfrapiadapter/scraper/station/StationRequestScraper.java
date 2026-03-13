package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.scraper.VerificationTokensScraper;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StationRequestScraper {
    private final VerificationTokensScraper verificationTokensScraper;
    private final ScraperUtils utils;

    public RequestStationTrainsDto scrapeRequestDetails(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);

        String date = verificationTokensScraper.scrapeDate(pageBody);
        String stationName = scrapeStationName(pageBody);

        String reCaptcha = verificationTokensScraper.scrapeReCaptcha(pageBody);
        String confirmationKey = verificationTokensScraper.scrapeConfirmationKey(pageBody);
        String isSearchWanted = verificationTokensScraper.scrapeIsSearchWanted(pageBody);
        String isReCaptchaFailed = verificationTokensScraper.scrapeIsReCaptchaFailed(pageBody);
        String requestVerificationToken = verificationTokensScraper.scrapeRequestVerificationToken(pageBody);

        return new RequestStationTrainsDto(date, stationName, reCaptcha, confirmationKey, isSearchWanted, isReCaptchaFailed,requestVerificationToken);
    }

    private String scrapeStationName(Element pageBody) {
        return pageBody.getElementById("StationName").attribute("value").getValue();
    }
}
