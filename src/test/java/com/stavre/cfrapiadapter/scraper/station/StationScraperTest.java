package com.stavre.cfrapiadapter.scraper.station;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.utils.ScraperUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class StationScraperTest {

    private final StationScraper scraper = new StationScraper(
            new StationTrainArrivalDepartureScraper(new StationTrainScraper(), new StationTrainMetadataScraper()),
            new ScraperUtils()
    );

    private String loadPage(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
    }

    @Test
    void scrapeArrivals_whenNoListGroupTable_returnsEmptyList() throws IOException {
        String html = loadPage("src/test/resources/scraper/station/station-no-trains-page.html");

        assertThat(scraper.scrapeArrivals(html)).isEmpty();
    }

    @Test
    void scrapeDepartures_whenNoListGroupTable_returnsEmptyList() throws IOException {
        String html = loadPage("src/test/resources/scraper/station/station-no-trains-page.html");

        assertThat(scraper.scrapeDepartures(html)).isEmpty();
    }
}
