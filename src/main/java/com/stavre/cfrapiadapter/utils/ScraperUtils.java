package com.stavre.cfrapiadapter.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.concurrent.Callable;

@Component
public class ScraperUtils {

    public String getOrBlank(Callable<String> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            return "";
        }
    }

    public Element scrapePageBody(String htmlPage) {
        return Jsoup.parse(htmlPage).body();
    }
}
