package com.stavre.cfrapiadapter.scraper.station;

import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StationTrainArrivalsScraperTest {
    public static final String OTHER_STATION = "Brașov";
    StationTrainMetadataScraper trainMetadataScraper = new StationTrainMetadataScraper();
    StationTrainScraper commonScraper = new StationTrainScraper();

    private final StationTrainArrivalDepartureScraper scraper = new StationTrainArrivalDepartureScraper(commonScraper, trainMetadataScraper);

    private Element loadElementFromFile(String filePath) throws IOException {
        String trainDepartureHtml = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);

        return Jsoup.parseBodyFragment(trainDepartureHtml).body().child(0);
    }

    @Test
    void stationTrainArrivalIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/arrival/train-arrival.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("11:36")
                .timeLabel("la timp")
                .train(new TrainMetadataDto("IR 11532", "11532", "IR", "Astra Trans Carpatic"))
                .mainStations("Brașov - Predeal - Azuga - Bușteni - Sinaia - Câmpina - Ploiești Vest - București Nord")
                .otherStation(OTHER_STATION)
                .platform("linia 1")
                .stopLabel("1 min (până la 11:37)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureNoPlatformIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/arrival/train-arrival-no-platform.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("12:43")
                .timeLabel("la timp*")
                .train(new TrainMetadataDto("IR 11534", "11534", "IR", "Astra Trans Carpatic"))
                .mainStations("Brașov - Predeal - Azuga - Bușteni - Sinaia - Câmpina - Ploiești Vest - București Nord")
                .otherStation(OTHER_STATION)
                .platform("")
                .stopLabel("1 min (până la 12:44)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureDelayedIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/arrival/train-arrival-delayed.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("11:14")
                .timeLabel("+2 min (întârziere)")
                .train(new TrainMetadataDto("R-E 3002", "3002", "R-E", "CFR Călători"))
                .mainStations("Brașov - Predeal - Azuga - Bușteni - Sinaia - Comarnic - Breaza hc - Câmpina - Florești Prahova - Ploiești Vest - Buftea - București Nord")
                .otherStation(OTHER_STATION)
                .platform("linia 3")
                .stopLabel("2 min (până la 11:16)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void stationTrainDepartureTerminusStationIsScraped() throws IOException {
        String trainDepartureFilePath = "src/test/resources/scraper/station/arrival/train-arrival-terminus-station.html";

        Element row = loadElementFromFile(trainDepartureFilePath);

        TrainArrivalDepartureDto expected = TrainArrivalDepartureDto.builder()
                .time("11:34")
                .timeLabel("la timp")
                .train(new TrainMetadataDto("R-E 11032", "11032", "R-E", "Regio Călători"))
                .mainStations("Brașov - Predeal - Azuga - Bușteni - Sinaia - Comarnic - Câmpina - Florești Prahova - Ploiești Vest - Buftea - Chitila - București Nord")
                .otherStation(OTHER_STATION)
                .platform("linia 4A")
                .stopLabel("necunoscută (stație terminus)")
                .build();


        TrainArrivalDepartureDto actual = scraper.scrapeTrainArrivalDeparture(row);

        assertThat(actual).isEqualTo(expected);
    }
}
