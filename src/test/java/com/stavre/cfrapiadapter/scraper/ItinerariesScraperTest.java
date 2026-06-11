package com.stavre.cfrapiadapter.scraper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.response.StationDto;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

class ItinerariesScraperTest {

    private final ItinerariesScraper scraper = new ItinerariesScraper();

    private String loadHtml() throws IOException {
        return FileUtils.readFileToString(
                new File("src/test/resources/scraper/itineraries/itineraries-page.html"),
                StandardCharsets.UTF_8);
    }

    @Test
    void scrapeStations_returnsCorrectStations() throws IOException {
        List<StationDto> result = scraper.scrapeStations(loadHtml());

        assertThat(result).containsExactly(
                new StationDto("Adjud", true),
                new StationDto("Arad", true),
                new StationDto("Bradu hc", false));
    }

    @Test
    void scrapeTrainNumbers_returnsCorrectTrainNumbers() throws IOException {
        List<String> result = scraper.scrapeTrainNumbers(loadHtml());

        assertThat(result).containsExactly("10101", "1743", "2850");
    }
}
