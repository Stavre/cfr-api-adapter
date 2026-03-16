package com.stavre.cfrapiadapter.scraper.train;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import com.stavre.cfrapiadapter.utils.ScraperUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

class TrainStopsScraperTest {
    public static final String LA_TIMP = "la timp*";
    private final TrainStopsScraper scraper = new TrainStopsScraper(new ScraperUtils());

    private Element loadElementFromFile(String filePath) throws IOException {
        String trainDepartureHtml = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        return Jsoup.parseBodyFragment(trainDepartureHtml).body().child(0);
    }

    @Test
    void scrapeTrainStopWithOnlyDeparture() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-1.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("")
                .arrivalTimeLabel("")
                .departureTime("19:39")
                .departureTimeLabel(LA_TIMP)
                .stationName("Curtici")
                .stationLabels(List.of("Trenul pleacă cu numărul IRN 079 în 15.03.2026"))
                .km("km 0")
                .stopDuration("")
                .platform("")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeTrainStopWithOnlyArrival() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-2.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("19:51")
                .arrivalTimeLabel(LA_TIMP)
                .departureTime("")
                .departureTimeLabel("")
                .stationName("Arad")
                .stationLabels(List.of("Trenul detașează vagoane pentru stația București Nord."))
                .km("km 17")
                .stopDuration("")
                .platform("")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeTrainStopWithArrivalAndDeparture() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-stop-arrival-departure.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("2:53")
                .arrivalTimeLabel(LA_TIMP)
                .departureTime("2:55")
                .departureTimeLabel(LA_TIMP)
                .stationName("Balota")
                .stationLabels(List.of())
                .km("km 303")
                .stopDuration("2 min oprire")
                .platform("")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeTrainStopWithArrivalDepartureAndPlatform() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-stop-arrival-departure-with-platform.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("8:21")
                .arrivalTimeLabel("la timp")
                .departureTime("8:23")
                .departureTimeLabel("la timp")
                .stationName("Caracal")
                .stationLabels(List.of())
                .km("km 156")
                .stopDuration("2 min oprire")
                .platform("linia 2")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeTrainStopWithArrivalDepartureAndMessages() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-stop-arrival-departure-messages.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("19:51")
                .arrivalTimeLabel(LA_TIMP)
                .departureTime("20:09")
                .departureTimeLabel(LA_TIMP)
                .stationName("Arad")
                .stationLabels(List.of("Trenul își schimbă numărul în IRN 79", "Trenul primește vagoane de la Oradea."))
                .km("km 17")
                .stopDuration("18 min oprire")
                .platform("")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeTrainStopWithArrivalDepartureAndPlatformDelay() throws IOException {
        // Load the HTML content from a file
        String trainStopsFilePath = "src/test/resources/scraper/train/train-stop-arrrival-departure-with-platform-delay.html";
        Element row = loadElementFromFile(trainStopsFilePath);

        // Define expected TrainStopDto
        Optional<TrainStopDto> expected = Optional.of(
                TrainStopDto.builder()
                .arrivalTime("7:03")
                .arrivalTimeLabel("+19 min (întârziere)")
                .departureTime("7:04")
                .departureTimeLabel("+20 min (întârziere)")
                .stationName("Roșiori Nord")
                .stationLabels(List.of())
                .km("km 100")
                .stopDuration("1 min oprire")
                .platform("linia 3")
                .build()
        );

        // Scrape the train stop
        Optional<TrainStopDto> actual = scraper.scrapeTrainStop(row);

        // Verify the scraped data
        assertThat(actual).isEqualTo(expected);
    }
}
