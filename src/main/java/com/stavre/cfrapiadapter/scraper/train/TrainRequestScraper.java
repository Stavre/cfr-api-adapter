package com.stavre.cfrapiadapter.scraper.train;

import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.scraper.VerificationTokensScraper;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainRequestScraper {
    private final VerificationTokensScraper verificationTokensScraper;
    private final ScraperUtils utils;

    public RequestTrainTimeTableDto scrapeRequestDetails(String htmlPage) {
        Element pageBody = utils.scrapePageBody(htmlPage);

        String date = verificationTokensScraper.scrapeDate(pageBody);
        String trainRunningNumber = scrapeTrainRunningNumber(pageBody);
        String selectedBranchCode = scrapeSelectedBranchCode(pageBody);
        String reCaptcha = verificationTokensScraper.scrapeReCaptcha(pageBody);
        String confirmationKey = verificationTokensScraper.scrapeConfirmationKey(pageBody);
        String isSearchWanted = verificationTokensScraper.scrapeIsSearchWanted(pageBody);
        String isReCaptchaFailed = verificationTokensScraper.scrapeIsReCaptchaFailed(pageBody);
        String requestVerificationToken = verificationTokensScraper.scrapeRequestVerificationToken(pageBody);

        return RequestTrainTimeTableDto.builder()
                .date(date)
                .trainRunningNumber(trainRunningNumber)
                .selectedBranchCode(selectedBranchCode)
                .recaptcha(reCaptcha)
                .confirmationKey(confirmationKey)
                .isSearchWanted(isSearchWanted)
                .isRecaptchaFailed(isReCaptchaFailed)
                .requestVerificationToken(requestVerificationToken)
                .build();
    }

    private String scrapeTrainRunningNumber(Element pageBody) {
        return pageBody.getElementById("TrainRunningNumber").attribute("value").getValue();
    }

    private String scrapeSelectedBranchCode(Element pageBody) {
        return pageBody.getElementById("SelectedBranchCode").attribute("value").getValue();
    }
}
