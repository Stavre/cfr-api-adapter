package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.request.RequestDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class VerificationTokensScraper {

    public RequestDto scrapeVerificationTokens(String htmlPage) {
        Element body = Jsoup.parse(htmlPage).body();

        String reCaptcha = body.getElementById("ReCaptcha").attribute("value").getValue();
        String confirmationKey = body.getElementById("ConfirmationKey").attribute("value").getValue();
        String isSearchWanted = body.getElementById("input-is-search-wanted").attribute("value").getValue();
        String isReCaptchaFailed = body.getElementById("input-recaptcha-failed").attribute("value").getValue();
        String requestVerificationToken = body.getElementsByAttributeValue("name", "__RequestVerificationToken").getFirst().attribute("value").getValue();

        return new RequestDto(reCaptcha, confirmationKey, isSearchWanted, isReCaptchaFailed, requestVerificationToken);
    }
}
