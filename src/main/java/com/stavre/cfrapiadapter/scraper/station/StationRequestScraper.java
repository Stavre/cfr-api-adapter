package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
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

        return RequestStationTrainsDto.builder()
                .date(date)
                .stationName(stationName)
                .recaptcha(reCaptcha)
                .confirmationKey(confirmationKey)
                .isSearchWanted(isSearchWanted)
                .isRecaptchaFailed(isReCaptchaFailed)
                .requestVerificationToken(requestVerificationToken)
                .build();
    }

    private String scrapeStationName(Element pageBody) {
        return pageBody.getElementById("StationName").attribute("value").getValue();
    }
}
