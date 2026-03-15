package com.stavre.cfrapiadapter.adapter;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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

        assertThat(result).isNotNull();

        assertThat(result.errors()).containsExactly("Could not scrape this train stop from CFR page.");
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

        // when
        var result = adapter.adapt(Optional.of(scraped), DATE);

        // then
        assertThat(result).isNotNull();

        assertThat(result.arrival()).isEqualTo(arrivalTs);
        assertThat(result.arrivalDelay()).isEqualTo(Duration.ofMinutes(2));

        assertThat(result.departure()).isEqualTo(departureTs);
        assertThat(result.departureDelay()).isEqualTo(Duration.ofMinutes(5));

        assertThat(result.station()).isEqualTo("Bucharest Nord");
        assertThat(result.journeyKm()).isEqualTo(123);
        assertThat(result.stopDuration()).isEqualTo(Duration.ofMinutes(2));
        assertThat(result.platform()).isEqualTo("5");

        assertThat(result.trainStopMessages()).containsExactly("label1", "label2");

        // no errors expected
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void adapt_whenSomeFieldsFail_parsesWhatItCanAndAddsErrors() {
        // given: invalid km and unknown stop duration, invalid platform format, timestamp parsing fails for arrival
        var scraped = new TrainStopDto(
                "bad-time",            // arrivalTime -> utils will return null and add errors
                "+3 min (întârziere)", // arrivalTimeLabel
                "15:00",               // departureTime
                "la timp",             // departureTimeLabel -> zero delay
                "   ",                 // stationName blank -> adapter will add error
                List.of(),             // stationLabels
                "not-a-number",        // km invalid
                "necunoscută",         // stopDuration unknown -> utils returns null
                "platform-bad-format"  // platform invalid -> utils will add error and return null
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

        // when
        var result = adapter.adapt(Optional.of(scraped), DATE);

        // then
        assertThat(result).isNotNull();

        // arrival timestamp failed -> null
        assertThat(result.arrival()).isNull();
        // arrival delay parsed
        assertThat(result.arrivalDelay()).isEqualTo(Duration.ofMinutes(3));

        // departure parsed
        assertThat(result.departure()).isEqualTo(LocalDateTime.of(2024, 1, 10, 15, 0));
        assertThat(result.departureDelay()).isEqualTo(Duration.ZERO);

        // station blank -> null
        assertThat(result.station()).isNull();

        // km invalid -> null
        assertThat(result.journeyKm()).isNull();

        // stopDuration "necunoscută" -> adapter's getStopDuration returns null
        assertThat(result.stopDuration()).isNull();

        // platform invalid -> null
        assertThat(result.platform()).isNull();

        // errors should contain messages for missing/invalid pieces
        assertThat(result.errors()).isNotEmpty();
        assertThat(result.errors()).contains(
                "Station name is blank",
                "Could not convert not-a-number to number of kilometers",
                "Stop duration necunoscută could not be converted to Duration"
        );
    }
}
