package com.stavre.cfrapiadapter.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class TrainAdapterTest {

    public static final String ON_TIME = "la timp*";
    public static final String CURTICI = "Curtici";
    public static final String BALOTA = "Balota";
    @Mock
    private TrainStopAdapter trainStopAdapter;

    @InjectMocks
    private TrainAdapter trainAdapter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdapt_withNonNullStops() {
        // Arrange
        TrainMetadataDto metadata =
                TrainMetadataDto.builder()
                        .id("IRN 1234")
                        .number("1234")
                        .category("IRN")
                        .operator("CFR Calatori")
                        .build();

        TrainStopDto stopDto1 = TrainStopDto.builder()
                .arrivalTime("19:39")
                .arrivalTimeLabel(ON_TIME)
                .departureTime("19:59")
                .departureTimeLabel(ON_TIME)
                .stationName(CURTICI)
                .km("0 km")
                .stopDuration("2 min")
                .platform("A")
                .build();

        TrainStopDto stopDto2 = TrainStopDto.builder()
                .arrivalTime("19:45")
                .arrivalTimeLabel(ON_TIME)
                .departureTime("19:55")
                .departureTimeLabel(ON_TIME)
                .stationName(BALOTA)
                .km("303 km")
                .stopDuration("2 min")
                .platform("B")
                .build();

        var branchStops = Map.of(
                TrainBranchDto.builder()
                        .name("main")
                        .originStation(CURTICI)
                        .destinationStation(BALOTA)
                        .build(),
                List.of(Optional.of(stopDto1), Optional.of(stopDto2)));

        EnrichedTrainStopDto expectedEnrichedStop1 = EnrichedTrainStopDto.builder()
                .arrival(LocalDateTime.parse("2026-03-15T19:39"))
                .departure(LocalDateTime.parse("2026-03-15T19:59"))
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(2))
                .platform("A")
                .trainStopMessages(List.of())
                .build();

        EnrichedTrainStopDto expectedEnrichedStop2 = EnrichedTrainStopDto.builder()
                .arrival(LocalDateTime.parse("2026-03-15T19:45"))
                .departure(LocalDateTime.parse("2026-03-15T19:55"))
                .station(BALOTA)
                .journeyKm(303)
                .stopDuration(Duration.ofMinutes(2))
                .platform("B")
                .trainStopMessages(List.of())
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> expectedAdaptedStops = new HashMap<>();
        expectedAdaptedStops.put(
                TrainBranchDto.builder()
                        .name("main")
                        .originStation(CURTICI)
                        .destinationStation(BALOTA)
                        .build(),
                List.of(expectedEnrichedStop1, expectedEnrichedStop2));

        TrainDto trainDto = new TrainDto(metadata, branchStops);

        when(trainStopAdapter.adapt(eq(Optional.of(stopDto1)), anyString())).thenReturn(expectedEnrichedStop1);
        when(trainStopAdapter.adapt(eq(Optional.of(stopDto2)), anyString())).thenReturn(expectedEnrichedStop2);

        // Act
        EnrichedTrainDto result = trainAdapter.adapt(trainDto, "15.03.2026");

        // Assert
        assertThat(expectedAdaptedStops).isEqualTo(result.stops());
    }

    @Test
    void testAdapt_withEmptyStops() {
        // Arrange
        String date = "15.03.2026";
        TrainMetadataDto metadata = new TrainMetadataDto("IRN 1234", "1234", "IRN", "CFR Calatori");
        Map<TrainBranchDto, List<Optional<TrainStopDto>>> branchStops = new HashMap<>();

        TrainDto trainDto = new TrainDto(metadata, branchStops);

        // Act
        EnrichedTrainDto result = trainAdapter.adapt(trainDto, date);

        // Assert
        assertThat(result.stops()).isEmpty();
    }
}