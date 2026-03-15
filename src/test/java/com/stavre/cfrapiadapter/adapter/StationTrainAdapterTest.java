package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDepartureDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class StationTrainAdapterTest {

    public static final String OTHER_STATION = "Arad";
    public static final List<String> STATIONS = List.of("stationA", "stationB");

    @Test
    void testAdapt_withBothInputsPresentAndMatching() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        TrainMetadataDto trainMetadataDto = new TrainMetadataDto("id", "number", "category", "operator");
        EnrichedTrainArrivalDepartureDto arrival = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(30))
                .platform("2B")
                .otherStation("Curtici")
                .train(trainMetadataDto)
                .mainStations(STATIONS)
                .stopDuration(Duration.ofMinutes(15))
                .build();

        EnrichedTrainArrivalDepartureDto departure = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(30))
                .platform("2B")
                .otherStation(OTHER_STATION)
                .train(trainMetadataDto)
                .mainStations(STATIONS)
                .stopDuration(Duration.ofMinutes(15))
                .build();

        Optional<EnrichedStationTrainDto> expected = Optional.of(
                EnrichedStationTrainDto.builder()
                        .arrival(arrival.timestamp())
                        .arrivalDelay(arrival.delay())
                        .fromStation(arrival.otherStation())

                        .departure(departure.timestamp())
                        .departureDelay(departure.delay())
                        .toStation(departure.otherStation())

                        .platform("2B")
                        .direction(STATIONS)
                        .stopDuration(Duration.ofMinutes(15))
                        .train(trainMetadataDto)
                        .errors(List.of())
                        .build()
        );

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.of(arrival), Optional.of(departure));

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withEmptyDeparture() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        TrainMetadataDto trainMetadataDto = new TrainMetadataDto("id", "number", "category", "operator");

        EnrichedTrainArrivalDepartureDto arrival = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(30))
                .platform("2A")
                .otherStation("Curtici")
                .train(trainMetadataDto)
                .mainStations(STATIONS)
                .stopDuration(Duration.ofMinutes(15))
                .build();

        Optional<EnrichedStationTrainDto> expected = Optional.of(
                EnrichedStationTrainDto.builder()
                        .arrival(arrival.timestamp())
                        .arrivalDelay(arrival.delay())
                        .fromStation(arrival.otherStation())

                        .departure(null)
                        .departureDelay(null)
                        .toStation(null)

                        .platform("2A")
                        .direction(STATIONS)
                        .stopDuration(Duration.ofMinutes(15))
                        .train(trainMetadataDto)
                        .errors(List.of("Missing departure information"))
                        .build()
        );

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.of(arrival), Optional.empty());

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withEmptyArrival() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        TrainMetadataDto trainMetadataDto = new TrainMetadataDto("id", "number", "category", "operator");

        EnrichedTrainArrivalDepartureDto departure = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(30))
                .platform("1D")
                .otherStation("Curtici")
                .train(new TrainMetadataDto("id", "number", "category", "operator"))
                .mainStations(STATIONS)
                .stopDuration(Duration.ofMinutes(15))
                .build();

        Optional<EnrichedStationTrainDto> expected = Optional.of(
                EnrichedStationTrainDto.builder()
                        .arrival(null)
                        .arrivalDelay(null)
                        .fromStation(null)

                        .departure(departure.timestamp())
                        .departureDelay(departure.delay())
                        .toStation(departure.otherStation())

                        .platform("1D")
                        .direction(STATIONS)
                        .stopDuration(Duration.ofMinutes(15))
                        .train(trainMetadataDto)
                        .errors(List.of("Missing arrival information"))
                        .build()
        );

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.empty(), Optional.of(departure));

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withBothInputsPresentAndNotMatching() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        TrainMetadataDto trainMetadataDto = new TrainMetadataDto("id", "number", "category", "operator");

        EnrichedTrainArrivalDepartureDto arrival = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(30))
                .platform("2")
                .otherStation("Curtici")
                .train(trainMetadataDto)
                .mainStations(STATIONS)
                .stopDuration(Duration.ofMinutes(15))
                .build();

        EnrichedTrainArrivalDepartureDto departure = EnrichedTrainArrivalDepartureDto.builder()
                .timestamp(LocalDateTime.now())
                .delay(Duration.ofMinutes(20)) // Different delay
                .platform("3") // Different platform
                .otherStation(OTHER_STATION)
                .train(trainMetadataDto)
                .mainStations(List.of("stationC", "stationD")) // Different direction
                .stopDuration(Duration.ofMinutes(10)) // Different stop duration
                .build();

        Optional<EnrichedStationTrainDto> expected = Optional.of(
                EnrichedStationTrainDto.builder()
                        .arrival(arrival.timestamp())
                        .arrivalDelay(arrival.delay())
                        .fromStation(arrival.otherStation())

                        .departure(departure.timestamp())
                        .departureDelay(departure.delay())
                        .toStation(departure.otherStation())

                        .platform(null)
                        .direction(null)
                        .stopDuration(null)
                        .train(trainMetadataDto)
                        .errors(List.of("Mismatched stopDuration: arrival=PT15M, departure=PT10M",
                                "Mismatched platform: arrival=2, departure=3",
                                "Mismatched direction (mainStations): arrival=[stationA, stationB], departure=[stationC, stationD]"))
                        .build()
        );

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.of(arrival), Optional.of(departure));

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testAdapt_withBothInputsEmpty() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.empty(), Optional.empty());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testAdapt_withBothInputsNull() {
        // Arrange
        StationTrainAdapter adapter = new StationTrainAdapter();

        // Act
        Optional<EnrichedStationTrainDto> result = adapter.adapt(Optional.empty(), Optional.empty());

        // Assert
        assertThat(result).isEmpty();
    }
}