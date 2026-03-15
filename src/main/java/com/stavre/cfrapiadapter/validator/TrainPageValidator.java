package com.stavre.cfrapiadapter.validator;

import com.stavre.cfrapiadapter.exception.CfrException;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class TrainPageValidator {

    private final ScraperUtils scraperUtils;

    public void validate(@NonNull String htmlPage) {
        Element pageBody = scraperUtils.scrapePageBody(htmlPage);

        pageBody.select("script").remove();

        System.out.println(pageBody);
        System.out.println("=========================================");


        System.out.println(pageBody.getElementsByClass("text-danger"));
        System.out.println("=========================================");
        System.out.println(pageBody.getElementsByClass("alert alert-warning"));

        List<String> errors = Stream.of(pageBody.getElementsByClass("text-danger"), pageBody.getElementsByClass("alert"))
                .flatMap(x -> x.stream())
                .map(Element::text)
                .filter(error -> error != null && !error.isBlank())
                .toList();

        if (errors.isEmpty()) {
            return;
        }

        throw new CfrException(errors);
    }
}
