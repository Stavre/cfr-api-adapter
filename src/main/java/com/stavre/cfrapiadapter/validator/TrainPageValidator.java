package com.stavre.cfrapiadapter.validator;

import com.stavre.cfrapiadapter.exception.CFRException;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TrainPageValidator {

    private final ScraperUtils scraperUtils;
    public void validate(@NonNull String htmlPage) {
        Element pageBody = scraperUtils.scrapePageBody(htmlPage);

        pageBody.select("script").remove();

        List<String> errors = pageBody
                .getElementsByClass("text-danger")
                .stream()
                .map(Element::text)
                .filter(error -> error != null && !error.isBlank())
                .toList();

        if (errors.isEmpty()) {
            return;
        }

        throw new CFRException(errors);
    }

    private boolean trainMissing(Element pageBody) {
        return pageBody.html().contains("Nu a fost găsit niciun tren cu acest număr!");
    }

    private boolean trainMissingOnDate(Element pageBody) {
        return pageBody.html().contains("nu circulă în data de");
    }

    private boolean requestOutsideOfTimeInterval(Element pageBody) {
        return pageBody.html().contains("Data nu se află în intervalul de 30 de zile!");
    }
}
