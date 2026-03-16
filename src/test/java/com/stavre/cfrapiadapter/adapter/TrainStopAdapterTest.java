package com.stavre.cfrapiadapter.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainStopAdapterTest {

    @Mock
    private AdapterUtils utils;

    @InjectMocks
    private TrainStopAdapter adapter;

    private static final String DATE = "10.01.2024";

    @Test
    void adapt_whenScrapedDtoMissing_returnsDtoWithError() {
        var result = adapter.adapt(Optional.empty(), DATE);

        var expected = new EnrichedTrainStopDto(List.of("Could not scrape this train stop from CFR page."));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void adapt_whenAllParsingSucceeds_returnsPopulatedEnrichedTrainStopDto() {
        // given
        var scraped = new TrainStopDto(
                "14:30",               // arrivalTime
                "+2 min (întârziere)", // arrivalTimeLabel
                "14:35",               // departureTime
                "+5 min (întârziere)", // departureTimeLabel
                " Bucharest Nord ",    // stationName (with surrounding spaces)
                List.of("label1", "label2"), // stationLabels
                "123km",               // km
                "2 min",               // stopDuration
                "linia 5"              // platform
        );

        // mock utils behaviour
        LocalDateTime arrivalTs = LocalDateTime.of(2024, 1, 10, 14, 30);
        LocalDateTime departureTs = LocalDateTime.of(2024, 1, 10, 14, 35);
        when(utils.getTimestamp(DATE, "14:30", new ArrayList<>()))
                .thenReturn(arrivalTs);
        when(utils.getTimestamp(DATE, "14:35", new ArrayList<>()))
                .thenReturn(departureTs);

        when(utils.getDelay("+2 min (întârziere)", new ArrayList<>()))
                .thenReturn(Duration.ofMinutes(2));
        when(utils.getDelay("+5 min (întârziere)", new ArrayList<>()))
                .thenReturn(Duration.ofMinutes(5));

        when(utils.getTrainPlatform("linia 5", new ArrayList<>()))
                .thenReturn("5");

        EnrichedTrainStopDto expected = EnrichedTrainStopDto.builder()
                .arrival(arrivalTs)
                .arrivalDelay(Duration.ofMinutes(2))
                .departure(departureTs)
                .departureDelay(Duration.ofMinutes(5))
                .station("Bucharest Nord")
                .journeyKm(123)
                .stopDuration(Duration.ofMinutes(2))
                .platform("5")
                .errors(List.of())
                .trainStopMessages(List.of("label1", "label2"))
                .build();

        // when
        var result = adapter.adapt(Optional.of(scraped), DATE);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void adapt_whenSomeFieldsFail_parsesWhatItCanAndAddsErrors() {
        // given
        var scraped = new TrainStopDto(
                "bad-time",
                "+3 min (întârziere)",
                "15:00",
                "la timp",
                "   ",
                List.of(),
                "not-a-number",
                "necunoscută",
                "platform-bad-format"
        );

        // mocks
        when(utils.getTimestamp(DATE, "bad-time", new ArrayList<>()))
                .thenReturn(null);
        when(utils.getTimestamp(DATE, "15:00", new ArrayList<>()))
                .thenReturn(LocalDateTime.of(2024, 1, 10, 15, 0));

        when(utils.getDelay("+3 min (întârziere)", new ArrayList<>()))
                .thenReturn(Duration.ofMinutes(3));
        when(utils.getDelay("la timp", new ArrayList<>()))
                .thenReturn(Duration.ZERO);

        lenient().when(utils.getTrainPlatform(eq("platform-bad-format"), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(null);

        EnrichedTrainStopDto expected = EnrichedTrainStopDto.builder()
                .arrival(null)
                .arrivalDelay(Duration.ofMinutes(3))
                .departure(LocalDateTime.of(2024, 1, 10, 15, 0))
                .departureDelay(Duration.ofMinutes(0))
                .station(null)
                .journeyKm(null)
                .stopDuration(null)
                .errors(List.of("Station name is blank",
                        "Could not convert not-a-number to number of kilometers",
                        "Stop duration necunoscută could not be converted to Duration"))
                .trainStopMessages(List.of())
                .build();

        // when
        var result = adapter.adapt(Optional.of(scraped), DATE);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
