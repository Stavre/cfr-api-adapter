package com.stavre.cfrapiadapter.scraper.train;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

class TrainBranchScraperTest {

    private final TrainBranchScraper scraper = new TrainBranchScraper();

    private Element loadBodyFromFile(String filePath) throws IOException {
        String html = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        return Jsoup.parse(html).body();
    }

    @Test
    void scrapeBranches_whenTrainHasMultipleBranches_returnsAllBranches() throws IOException {
        Element body = loadBodyFromFile(
                "src/test/resources/scraper/train/train-04105-timetable-page.html");

        List<TrainBranchDto> actual = scraper.scrapeBranches(body);

        List<TrainBranchDto> expected = List.of(
                new TrainBranchDto("Tren principal", "Bistrița Nord", "Cluj Napoca"),
                new TrainBranchDto("Grupă", "Ilva Mică", "Cluj Napoca")
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scrapeBranches_whenTrainHasNoBranchInfo_returnsMainBranchFallback() throws IOException {
        Element body = loadBodyFromFile(
                "src/test/resources/scraper/train/train-10101-timetable-page.html");

        List<TrainBranchDto> actual = scraper.scrapeBranches(body);

        assertThat(actual).isEqualTo(List.of(new TrainBranchDto("Main branch", null, null)));
    }
}
