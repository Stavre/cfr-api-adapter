package com.stavre.cfrapiadapter.scraper;

import com.stavre.cfrapiadapter.dto.response.StationDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ItinerariesScraper {

    private static final Pattern STATION_PATTERN =
            Pattern.compile("\\{name: \"([^\"]+)\", isImportant: (true|false)\\}");
    private static final Pattern TRAIN_PATTERN = Pattern.compile("\"([^\"]+)\"");
    private static final String TRAINS_VAR_PREFIX = "var availableTrains = [";
    private static final String ARRAY_END = "];";

    public List<StationDto> scrapeStations(String html) {
        String scriptContent = findScriptContent(html);
        List<StationDto> stations = new ArrayList<>();
        Matcher matcher = STATION_PATTERN.matcher(scriptContent);
        while (matcher.find()) {
            stations.add(new StationDto(matcher.group(1), Boolean.parseBoolean(matcher.group(2))));
        }
        return stations;
    }

    public List<String> scrapeTrainNumbers(String html) {
        String scriptContent = findScriptContent(html);
        int start = scriptContent.indexOf(TRAINS_VAR_PREFIX) + TRAINS_VAR_PREFIX.length();
        int end = scriptContent.indexOf(ARRAY_END, start);
        String arrayContent = scriptContent.substring(start, end);
        List<String> trains = new ArrayList<>();
        Matcher matcher = TRAIN_PATTERN.matcher(arrayContent);
        while (matcher.find()) {
            trains.add(matcher.group(1));
        }
        return trains;
    }

    private String findScriptContent(String html) {
        Document doc = Jsoup.parse(html);
        for (Element script : doc.select("script")) {
            if (script.data().contains("availableStations")) {
                return script.data();
            }
        }
        return "";
    }
}
