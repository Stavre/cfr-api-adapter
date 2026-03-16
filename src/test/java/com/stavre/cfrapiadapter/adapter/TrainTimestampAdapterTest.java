package com.stavre.cfrapiadapter.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class TrainTimestampAdapterTest {

    public static final String CURTICI = "Curtici";
    public static final String BALOTA = "Balota";
    public static final String CURTICI_BALOTA = "Curtici - Balota";
    public static final TrainMetadataDto TRAIN_METADATA_DTO = TrainMetadataDto.builder()
            .id("IRN 1234")
            .number("1234")
            .category("IRN")
            .operator("CFR Calatori")
            .build();
    private final TrainTimestampAdapter trainTimestampAdapter = new TrainTimestampAdapter();

    @Test
    void testAccountForMultipleDayJourney() {
        // Arrange
        LocalDateTime arrival1 = LocalDateTime.of(2026, 3, 15, 23, 0);
        LocalDateTime departure1 = LocalDateTime.of(2026, 3, 15, 23, 30);

        LocalDateTime arrival2 = LocalDateTime.of(2026, 3, 15, 0, 10);
        LocalDateTime departure2 = LocalDateTime.of(2026, 3, 15, 0, 40);

        EnrichedTrainStopDto stop1 = EnrichedTrainStopDto.builder()
                .arrival(arrival1)
                .departure(departure1)
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(30))
                .platform("A")
                .build();

        EnrichedTrainStopDto stop2 = EnrichedTrainStopDto.builder()
                .arrival(arrival2)
                .departure(departure2)
                .station(BALOTA)
                .journeyKm(303)
                .stopDuration(Duration.ofMinutes(30))
                .platform("B")
                .build();

        EnrichedTrainStopDto expectedStop2 = stop2
                .withArrival(arrival2.plusDays(1))
                .withDeparture(departure2.plusDays(1));

        TrainBranchDto branch = TrainBranchDto
                .builder()
                .name(CURTICI_BALOTA)
                .originStation(CURTICI)
                .destinationStation(BALOTA)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops = Map.of(branch, List.of(stop1, stop2));

        EnrichedTrainDto trainDto = EnrichedTrainDto.builder()
                .stops(stops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> expectedStops = Map.of(branch, List.of(stop1, expectedStop2));

        EnrichedTrainDto expectedTrainDto = EnrichedTrainDto.builder()
                .stops(expectedStops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        // Act
        EnrichedTrainDto result = trainTimestampAdapter.accountForMultipleDayJourney(trainDto);

        // Assert
        assertThat(expectedTrainDto).isEqualTo(result);
    }

    @Test
    void testAccountForMultipleDayJourneySecondDepartureNextDay() {
        // Arrange
        LocalDateTime arrival1 = LocalDateTime.of(2026, 3, 15, 20, 0);
        LocalDateTime departure1 = LocalDateTime.of(2026, 3, 15, 20, 30);

        LocalDateTime arrival2 = LocalDateTime.of(2026, 3, 15, 23, 40);
        LocalDateTime departure2 = LocalDateTime.of(2026, 3, 15, 0, 10);

        EnrichedTrainStopDto stop1 = EnrichedTrainStopDto.builder()
                .arrival(arrival1)
                .departure(departure1)
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(30))
                .platform("A")
                .build();

        EnrichedTrainStopDto stop2 = EnrichedTrainStopDto.builder()
                .arrival(arrival2)
                .departure(departure2)
                .station(BALOTA)
                .journeyKm(303)
                .stopDuration(Duration.ofMinutes(30))
                .platform("B")
                .build();

        EnrichedTrainStopDto expectedStop2 = stop2
                .withArrival(arrival2)
                .withDeparture(departure2.plusDays(1));

        TrainBranchDto branch = TrainBranchDto
                .builder()
                .name(CURTICI_BALOTA)
                .originStation(CURTICI)
                .destinationStation(BALOTA)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops = Map.of(branch, List.of(stop1, stop2));

        EnrichedTrainDto trainDto = EnrichedTrainDto.builder()
                .stops(stops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> expectedStops = Map.of(branch, List.of(stop1, expectedStop2));

        EnrichedTrainDto expectedTrainDto = EnrichedTrainDto.builder()
                .stops(expectedStops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        // Act
        EnrichedTrainDto result = trainTimestampAdapter.accountForMultipleDayJourney(trainDto);

        // Assert
        assertThat(expectedTrainDto).isEqualTo(result);
    }

    @Test
    void testAccountForMultipleDayJourneyFirstDepartureNextDay() {
        // Arrange
        LocalDateTime arrival1 = LocalDateTime.of(2026, 3, 15, 23, 40);
        LocalDateTime departure1 = LocalDateTime.of(2026, 3, 15, 0, 10);

        LocalDateTime arrival2 = LocalDateTime.of(2026, 3, 15, 2, 40);
        LocalDateTime departure2 = LocalDateTime.of(2026, 3, 15, 3, 10);

        EnrichedTrainStopDto stop1 = EnrichedTrainStopDto.builder()
                .arrival(arrival1)
                .departure(departure1)
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(30))
                .platform("A")
                .build();

        EnrichedTrainStopDto stop2 = EnrichedTrainStopDto.builder()
                .arrival(arrival2)
                .departure(departure2)
                .station(BALOTA)
                .journeyKm(303)
                .stopDuration(Duration.ofMinutes(30))
                .platform("B")
                .build();

        EnrichedTrainStopDto expectedStop1 = stop1
                .withArrival(arrival1)
                .withDeparture(departure1.plusDays(1));

        EnrichedTrainStopDto expectedStop2 = stop2
                .withArrival(arrival2.plusDays(1))
                .withDeparture(departure2.plusDays(1));

        TrainBranchDto branch = TrainBranchDto
                .builder()
                .name(CURTICI_BALOTA)
                .originStation(CURTICI)
                .destinationStation(BALOTA)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops = Map.of(branch, List.of(stop1, stop2));

        TrainMetadataDto trainMetadataDto = TrainMetadataDto.builder()
                .id("IRN 1234")
                .number("1234")
                .category("IRN")
                .operator("CFR Calatori")
                .build();

        EnrichedTrainDto trainDto = EnrichedTrainDto.builder()
                .stops(stops)
                .metadata(trainMetadataDto)
                .build();

        var expectedStops = Map.of(branch, List.of(expectedStop1, expectedStop2));

        EnrichedTrainDto expectedTrainDto = EnrichedTrainDto.builder()
                .stops(expectedStops)
                .metadata(trainMetadataDto)
                .build();

        // Act
        EnrichedTrainDto result = trainTimestampAdapter.accountForMultipleDayJourney(trainDto);

        // Assert
        assertThat(expectedTrainDto).isEqualTo(result);
    }

    @Test
    void testAccountForOneDayJourneyWithTwoStops() {
        // Arrange
        LocalDateTime arrival1 = LocalDateTime.of(2026, 3, 15, 20, 0);
        LocalDateTime departure1 = LocalDateTime.of(2026, 3, 15, 20, 30);

        LocalDateTime arrival2 = LocalDateTime.of(2026, 3, 15, 21, 10);
        LocalDateTime departure2 = LocalDateTime.of(2026, 3, 15, 21, 40);

        EnrichedTrainStopDto stop1 = EnrichedTrainStopDto.builder()
                .arrival(arrival1)
                .departure(departure1)
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(30))
                .platform("A")
                .build();

        EnrichedTrainStopDto stop2 = EnrichedTrainStopDto.builder()
                .arrival(arrival2)
                .departure(departure2)
                .station(BALOTA)
                .journeyKm(303)
                .stopDuration(Duration.ofMinutes(30))
                .platform("B")
                .build();

        TrainBranchDto branch = TrainBranchDto
                .builder()
                .name(CURTICI_BALOTA)
                .originStation(CURTICI)
                .destinationStation(BALOTA)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops = Map.of(branch, List.of(stop1, stop2));

        EnrichedTrainDto trainDto = EnrichedTrainDto.builder()
                .stops(stops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> expectedStops = Map.of(branch, List.of(stop1, stop2));

        EnrichedTrainDto expectedTrainDto = EnrichedTrainDto.builder()
                .stops(expectedStops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        // Act
        EnrichedTrainDto result = trainTimestampAdapter.accountForMultipleDayJourney(trainDto);

        // Assert
        assertThat(expectedTrainDto).isEqualTo(result);
    }

    @Test
    void testAccountForOneDayJourneyWithSingleStop() {
        // Arrange
        LocalDateTime arrival1 = LocalDateTime.of(2026, 3, 15, 23, 0); // 11 PM on March 15th

        EnrichedTrainStopDto stop1 = EnrichedTrainStopDto.builder()
                .arrival(arrival1)
                .station(CURTICI)
                .journeyKm(0)
                .stopDuration(Duration.ofMinutes(30))
                .platform("A")
                .build();

        TrainBranchDto branch = TrainBranchDto
                .builder()
                .name(CURTICI_BALOTA)
                .originStation(CURTICI)
                .destinationStation(BALOTA)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> stops = Map.of(branch, List.of(stop1));

        EnrichedTrainDto trainDto = EnrichedTrainDto.builder()
                .stops(stops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        Map<TrainBranchDto, List<EnrichedTrainStopDto>> expectedStops = Map.of(branch, List.of(stop1));

        EnrichedTrainDto expectedTrainDto = EnrichedTrainDto.builder()
                .stops(expectedStops)
                .metadata(TRAIN_METADATA_DTO)
                .build();

        // Act
        EnrichedTrainDto result = trainTimestampAdapter.accountForMultipleDayJourney(trainDto);

        // Assert
        assertThat(expectedTrainDto).isEqualTo(result);
    }
}