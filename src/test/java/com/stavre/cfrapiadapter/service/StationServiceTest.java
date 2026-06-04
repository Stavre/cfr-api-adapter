package com.stavre.cfrapiadapter.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class StationServiceTest {

    private final StationService service = new StationService(null, null, null);

    private static final EnrichedStationTrainDto TRAIN_WITH_BOTH = EnrichedStationTrainDto.builder()
            .arrival(LocalDateTime.of(2026, 6, 4, 10, 0))
            .departure(LocalDateTime.of(2026, 6, 4, 10, 5))
            .build();

    private static final EnrichedStationTrainDto ARRIVAL_ONLY = EnrichedStationTrainDto.builder()
            .arrival(LocalDateTime.of(2026, 6, 4, 11, 0))
            .build();

    private static final EnrichedStationTrainDto DEPARTURE_ONLY = EnrichedStationTrainDto.builder()
            .departure(LocalDateTime.of(2026, 6, 4, 12, 0))
            .build();

    @Test
    void getArrivals_returnOnlyEntriesWithNonNullArrival() {
        List<EnrichedStationTrainDto> result =
                service.getArrivals(List.of(TRAIN_WITH_BOTH, ARRIVAL_ONLY, DEPARTURE_ONLY));

        assertThat(result).containsExactlyInAnyOrder(TRAIN_WITH_BOTH, ARRIVAL_ONLY);
    }

    @Test
    void getArrivals_whenEmptyList_returnsEmptyList() {
        assertThat(service.getArrivals(List.of())).isEmpty();
    }

    @Test
    void getDepartures_returnsOnlyEntriesWithNonNullDeparture() {
        List<EnrichedStationTrainDto> result =
                service.getDepartures(List.of(TRAIN_WITH_BOTH, ARRIVAL_ONLY, DEPARTURE_ONLY));

        assertThat(result).containsExactlyInAnyOrder(TRAIN_WITH_BOTH, DEPARTURE_ONLY);
    }

    @Test
    void getDepartures_whenEmptyList_returnsEmptyList() {
        assertThat(service.getDepartures(List.of())).isEmpty();
    }
}
