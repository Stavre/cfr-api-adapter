package com.stavre.cfrapiadapter.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import com.stavre.cfrapiadapter.utils.AdapterUtils;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EnrichedTrainArrivalDepartureAdapterTest {

    public static final String STOP_LABEL = "2 min oprire";
    public static final String MAIN_STATIONS = "stationA-stationB";
    public static final String PLATFORM = "linia 2";
    public static final String DATE = "10.01.2024";
    public static final TrainMetadataDto TRAIN_METADATA_DTO = TrainMetadataDto.builder()
                                                                                .id("id")
                                                                                .number("number")
                                                                                .category("category")
                                                                                .operator("operator")
                                                                                .build();
    public static final String LA_TIMP = "la timp*";
    public static final String OTHER_STATION = "Curtici";
    public static final String TIME = "19:30";
    public static final List<String> STATIONS = List.of("stationA", "stationB");

    private final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    private final AdapterUtils adapterUtils = new AdapterUtils(dateTimeUtils);

    @InjectMocks
    private EnrichedTrainArrivalDepartureAdapter adapter = new EnrichedTrainArrivalDepartureAdapter(adapterUtils);

    @Test
    void testAdapt_withNullInput() {
        // Arrange
        TrainArrivalDepartureDto arrival = null;

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testAdapt_withEmptyStrings() {

        // Arrange
        TrainMetadataDto trainMetadata = new TrainMetadataDto("", "", "", "");
        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time("")
                .timeLabel("")
                .platform("")
                .otherStation("")
                .train(trainMetadata)
                .mainStations("")
                .stopLabel("")
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(null)
                        .delay(null)
                        .platform(null)
                        .otherStation(null)
                        .train(trainMetadata)
                        .mainStations(List.of())
                        .stopDuration(null)
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withFullData() {
        // Arrange
        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time(TIME)
                .timeLabel(LA_TIMP)
                .platform(PLATFORM)
                .otherStation(OTHER_STATION)
                .train(TRAIN_METADATA_DTO)
                .mainStations(MAIN_STATIONS)
                .stopLabel(STOP_LABEL)
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(LocalDate.of(2024, 1, 10).atTime(19, 30))
                        .delay(Duration.ofMinutes(0))
                        .platform("2")
                        .otherStation(OTHER_STATION)
                        .train(TRAIN_METADATA_DTO)
                        .mainStations(Arrays.stream(MAIN_STATIONS.split("-")).toList())
                        .stopDuration(Duration.ofMinutes(2))
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withEmptyDelay() {
        // Arrange
        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time("5:30")
                .timeLabel("")
                .platform(PLATFORM)
                .otherStation(OTHER_STATION)
                .train(TRAIN_METADATA_DTO)
                .mainStations("stationA - stationB")
                .stopLabel(STOP_LABEL)
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(LocalDate.of(2024, 1, 10).atTime(5, 30))
                        .delay(null)
                        .platform("2")
                        .otherStation(OTHER_STATION)
                        .train(TRAIN_METADATA_DTO)
                        .mainStations(STATIONS)
                        .stopDuration(Duration.ofMinutes(2))
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withEmptyPlatform() {
        // Arrange
        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time(TIME)
                .timeLabel(LA_TIMP)
                .platform("")
                .otherStation(OTHER_STATION)
                .train(TRAIN_METADATA_DTO)
                .mainStations(MAIN_STATIONS)
                .stopLabel(STOP_LABEL)
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(LocalDate.of(2024, 1, 10).atTime(19, 30))
                        .delay(Duration.ofMinutes(0))
                        .platform(null)
                        .otherStation(OTHER_STATION)
                        .train(TRAIN_METADATA_DTO)
                        .mainStations(STATIONS)
                        .stopDuration(Duration.ofMinutes(2))
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);

    }

    @Test
    void testAdapt_withAllData() {
        // Arrange

        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time(TIME)
                .timeLabel(LA_TIMP)
                .platform(PLATFORM)
                .otherStation(OTHER_STATION)
                .train(TRAIN_METADATA_DTO)
                .mainStations(MAIN_STATIONS)
                .stopLabel(STOP_LABEL)
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(LocalDate.of(2024, 1, 10).atTime(19, 30))
                        .delay(Duration.ofMinutes(0))
                        .platform("2")
                        .otherStation(OTHER_STATION)
                        .train(TRAIN_METADATA_DTO)
                        .mainStations(STATIONS)
                        .stopDuration(Duration.ofMinutes(2))
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);

    }

    @Test
    void testAdapt_withNullPlatform() {
        // Arrange
        TrainArrivalDepartureDto arrival = TrainArrivalDepartureDto.builder()
                .time(TIME)
                .timeLabel(LA_TIMP)
                .platform(null)
                .otherStation(OTHER_STATION)
                .train(TRAIN_METADATA_DTO)
                .mainStations(MAIN_STATIONS)
                .stopLabel(STOP_LABEL)
                .build();

        Optional<EnrichedTrainArrivalDepartureDto> expected = Optional.of(
                EnrichedTrainArrivalDepartureDto.builder()
                        .timestamp(LocalDate.of(2024, 1, 10).atTime(19, 30))
                        .delay(Duration.ofMinutes(0))
                        .platform(null)
                        .otherStation(OTHER_STATION)
                        .train(TRAIN_METADATA_DTO)
                        .mainStations(STATIONS)
                        .stopDuration(Duration.ofMinutes(2))
                        .build()
        );

        // Act
        Optional<EnrichedTrainArrivalDepartureDto> result = adapter.adapt(arrival, DATE);

        // Assert
        assertThat(result).isEqualTo(expected);

    }
}