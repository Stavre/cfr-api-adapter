package com.stavre.cfrapiadapter.scraper.station;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class StationTrainDeparturesScraperTest {
    StationTrainMetadataScraper trainMetadataScraper = new StationTrainMetadataScraper();
    StationTrainScraper commonScraper = new StationTrainScraper();

    private final StationTrainArrivalDepartureScraper scraper =
            new StationTrainArrivalDepartureScraper(commonScraper, trainMetadataScraper);

    private Element loadElementFromFile(String filePath) throws IOException {
        String trainDepartureHtml = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);

        return Jsoup.parseBodyFragment(trainDepartureHtml).body().child(0);
    }

    @Test
    void stationTrainDepartureIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("6:09")
                .timeLabel("la timp")
                .train(new TrainMetadataDto("R 10222", "10222", "R", "Transferoviar Călători"))
                .mainStations("Slănic - Prăjani hc - Ploiești Vest - Ploiești Sud")
                .otherStation("Ploiești Sud")
                .platform("linia 3")
                .stopLabel("1 min (începând cu 6:08)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureNoPlatformIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-no-platform.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("17:30")
                .timeLabel("la timp*")
                .train(new TrainMetadataDto("R 9621", "9621", "R", "CFR Călători"))
                .mainStations("Lugoj - Boldur H - Buziaș - Timișoara Nord")
                .otherStation("Timișoara Nord")
                .platform("")
                .stopLabel("1 min (începând cu 17:29)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureDelayedIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-delayed.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("6:05")
                .timeLabel("+10 min (întârziere)")
                .train(new TrainMetadataDto("R-E 5025", "5025", "R-E", "CFR Călători"))
                .mainStations("Ploiești Sud - Ploiești Vest - Buftea - Chitila - București Nord")
                .otherStation("București Nord")
                .platform("linia 1")
                .stopLabel("18 min (începând cu 5:47)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureOriginStationIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/departure/train-departure-origin-station.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("7:06")
                .timeLabel("la timp")
                .train(new TrainMetadataDto("R-E 11052", "11052", "R-E", "Regio Călători"))
                .mainStations("Ploiești Vest - Ploiești Sud - Mizil - Buzău - Râmnicu Sărat - Focșani - Mărășești")
                .otherStation("Mărășești")
                .platform("linia 4")
                .stopLabel("necunoscută (stație terminus)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }
}
