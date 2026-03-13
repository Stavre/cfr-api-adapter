package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.request.RequestDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
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

    public String scrapeReCaptcha(Element pageBody) {
        return pageBody.getElementById("ReCaptcha").attribute("value").getValue();
    }

    public String scrapeConfirmationKey(Element pageBody) {
        return pageBody.getElementById("ConfirmationKey").attribute("value").getValue();
    }

    public String scrapeIsSearchWanted(Element pageBody) {
        return pageBody.getElementById("input-is-search-wanted").attribute("value").getValue();
    }

    public String scrapeIsReCaptchaFailed(Element pageBody) {
        return pageBody.getElementById("input-recaptcha-failed").attribute("value").getValue();
    }

    public String scrapeRequestVerificationToken(Element pageBody) {
        return pageBody.getElementsByAttributeValue("name", "__RequestVerificationToken").getFirst().attribute("value").getValue();
    }

    public String scrapeDate(Element pageBody) {
        return pageBody.getElementById("Date").attribute("value").getValue();
    }
}
