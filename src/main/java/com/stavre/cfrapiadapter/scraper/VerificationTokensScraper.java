package com.stavre.cfrapiadapter.scraper;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class VerificationTokensScraper {

    private static final String ATTRIBUTE_KEY = "value";

    public String scrapeReCaptcha(Element pageBody) {
        return pageBody.getElementById("ReCaptcha").attr(ATTRIBUTE_KEY);
    }

    public String scrapeConfirmationKey(Element pageBody) {
        return pageBody.getElementById("ConfirmationKey").attr(ATTRIBUTE_KEY);
    }

    public String scrapeIsSearchWanted(Element pageBody) {
        return pageBody.getElementById("input-is-search-wanted").attr(ATTRIBUTE_KEY);
    }

    public String scrapeIsReCaptchaFailed(Element pageBody) {
        return pageBody.getElementById("input-recaptcha-failed").attr(ATTRIBUTE_KEY);
    }

    public String scrapeRequestVerificationToken(Element pageBody) {
        return pageBody
                .getElementsByAttributeValue("name", "__RequestVerificationToken")
                .getFirst()
                .attr(ATTRIBUTE_KEY);
    }

    public String scrapeDate(Element pageBody) {
        return pageBody.getElementById("Date").attr(ATTRIBUTE_KEY);
    }
}