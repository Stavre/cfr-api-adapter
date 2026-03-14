package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class StationTrainDeparturesScraperTest {
    StationTrainMetadataScraper trainMetadataScraper = new StationTrainMetadataScraper();
    StationTrainScraper commonScraper = new StationTrainScraper();

    private final StationTrainDeparturesScraper scraper = new StationTrainDeparturesScraper(commonScraper, trainMetadataScraper);

    private Element loadElementFromFile(String filePath) throws IOException {
        String trainDepartureHtml = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);

        return Jsoup.parseBodyFragment(trainDepartureHtml).body().child(0);
    }

    @Test
    void stationTrainDepartureIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        Optional<TrainDepartureDto> trainDepartureDtoOptional = scraper.scrapeDepartureTrain(row);

        assertThat(trainDepartureDtoOptional.isPresent()).isTrue();

        TrainDepartureDto expected = TrainDepartureDto.builder()
                .departureTime("6:09")
                .departureTimeLabel("la timp")
                .train(new TrainMetadataDto("10222", "R", "Transferoviar Călători"))
                .mainStations("Slănic - Prăjani hc - Ploiești Vest - Ploiești Sud")
                .destinationName("Ploiești Sud")
                .platform("linia 3")
                .stopLabel("1 min (începând cu 6:08)")
                .build();


        TrainDepartureDto actual = trainDepartureDtoOptional.get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureNoPlatformIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-no-platform.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        Optional<TrainDepartureDto> trainDepartureDtoOptional = scraper.scrapeDepartureTrain(row);

        assertThat(trainDepartureDtoOptional.isPresent()).isTrue();

        TrainDepartureDto expected = TrainDepartureDto.builder()
                .departureTime("17:30")
                .departureTimeLabel("la timp*")
                .train(new TrainMetadataDto("9621", "R", "CFR Călători"))
                .mainStations("Lugoj - Boldur H - Buziaș - Timișoara Nord")
                .destinationName("Timișoara Nord")
                .platform("")
                .stopLabel("1 min (începând cu 17:29)")
                .build();


        TrainDepartureDto actual = trainDepartureDtoOptional.get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureDelayedIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-delayed.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        Optional<TrainDepartureDto> trainDepartureDtoOptional = scraper.scrapeDepartureTrain(row);

        assertThat(trainDepartureDtoOptional.isPresent()).isTrue();

        TrainDepartureDto expected = TrainDepartureDto.builder()
                .departureTime("6:05")
                .departureTimeLabel("+10 min (întârziere)")
                .train(new TrainMetadataDto("5025", "R-E", "CFR Călători"))
                .mainStations("Ploiești Sud - Ploiești Vest - Buftea - Chitila - București Nord")
                .destinationName("București Nord")
                .platform("linia 1")
                .stopLabel("18 min (începând cu 5:47)")
                .build();


        TrainDepartureDto actual = trainDepartureDtoOptional.get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureOriginStationIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-origin-station.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        Optional<TrainDepartureDto> trainDepartureDtoOptional = scraper.scrapeDepartureTrain(row);

        assertThat(trainDepartureDtoOptional.isPresent()).isTrue();

        TrainDepartureDto expected = TrainDepartureDto.builder()
                .departureTime("7:06")
                .departureTimeLabel("la timp")
                .train(new TrainMetadataDto("11052", "R-E", "Regio Călători"))
                .mainStations("Ploiești Vest - Ploiești Sud - Mizil - Buzău - Râmnicu Sărat - Focșani - Mărășești")
                .destinationName("Mărășești")
                .platform("linia 4")
                .stopLabel("necunoscută (stație terminus)")
                .build();


        TrainDepartureDto actual = trainDepartureDtoOptional.get();

        assertThat(actual).isEqualTo(expected);
    }
}
