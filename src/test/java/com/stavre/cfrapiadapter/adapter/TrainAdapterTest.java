package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainBranchDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainMetadataDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainStopDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainAdapterTest {

    @Mock
    private TrainStopAdapter trainStopAdapter;

    @InjectMocks
    private TrainAdapter adapter;

    @Test
    void adapt_mapsMetadataAndStops() {
        // arrange
        TrainDto dto = org.mockito.Mockito.mock(TrainDto.class);
        TrainBranchDto branch = org.mockito.Mockito.mock(TrainBranchDto.class);

        TrainMetadataDto metadata = new TrainMetadataDto("id-1", "123", "IC", "CFR");
        when(dto.metadata()).thenReturn(metadata);

        when(dto.branchStops()).thenReturn(Map.of(branch, List.of(Optional.empty())));

        EnrichedTrainStopDto enrichedStop = org.mockito.Mockito.mock(EnrichedTrainStopDto.class);
        when(trainStopAdapter.adapt(any(Optional.class), eq("2024-01-10"))).thenReturn(enrichedStop);

        // act
        EnrichedTrainDto result = adapter.adapt(dto, "2024-01-10");

        // assert
        assertThat(result).isNotNull();
        assertThat(result.metadata()).isEqualTo(metadata);
        assertThat(result.stops()).containsKey(branch);
        assertThat(result.stops().get(branch)).containsExactly(enrichedStop);
    }

    @Test
    void adapt_normalizesArrivalTimestampsWhenTimeWrapsToNextDay() {
        // arrange
        TrainDto dto = org.mockito.Mockito.mock(TrainDto.class);
        TrainBranchDto branch = org.mockito.Mockito.mock(TrainBranchDto.class);

        TrainMetadataDto metadata2 = new TrainMetadataDto("id-2", "456", "R", "Operator");
        when(dto.metadata()).thenReturn(metadata2);
        // two stops for the same branch
        when(dto.branchStops()).thenReturn(Map.of(branch, List.of(Optional.of(org.mockito.Mockito.mock(TrainStopDto.class)),
                Optional.of(org.mockito.Mockito.mock(TrainStopDto.class)))));

        // create mocks for enriched stops
        EnrichedTrainStopDto stop1 = org.mockito.Mockito.mock(EnrichedTrainStopDto.class);
        EnrichedTrainStopDto stop2 = org.mockito.Mockito.mock(EnrichedTrainStopDto.class);
        EnrichedTrainStopDto adjustedStop2 = org.mockito.Mockito.mock(EnrichedTrainStopDto.class);

        // stop1 arrival = 2024-01-10T23:50
        when(stop1.arrival()).thenReturn(LocalDateTime.of(2024, 1, 10, 23, 50));
        // stop2 arrival originally = 2024-01-10T00:10 (same date, earlier time)
        when(stop2.arrival()).thenReturn(LocalDateTime.of(2024, 1, 10, 0, 10));
        // when normalization calls stop2.withArrival(adjusted) return adjustedStop2
        when(stop2.withArrival(any(LocalDateTime.class))).thenReturn(adjustedStop2);
        // adjustedStop2 arrival should be 2024-01-11T00:10
        when(adjustedStop2.arrival()).thenReturn(LocalDateTime.of(2024, 1, 11, 0, 10));

        // stub trainStopAdapter.adapt to return stop1 then stop2 for the two optionals
        when(trainStopAdapter.adapt(any(Optional.class), eq("2024-01-10")))
                .thenReturn(stop1, stop2);

        // act
        EnrichedTrainDto result = adapter.adapt(dto, "2024-01-10");

        // assert
        List<EnrichedTrainStopDto> stops = result.stops().get(branch);
        assertThat(stops).hasSize(2);
        // first stop unchanged
        assertThat(stops.get(0)).isSameAs(stop1);
        // second stop should be the adjusted instance returned by withArrival
        assertThat(stops.get(1)).isSameAs(adjustedStop2);
        // verify the adjusted arrival timestamp is the next day at 00:10
        assertThat(stops.get(1).arrival()).isEqualTo(LocalDateTime.of(2024, 1, 11, 0, 10));
    }
}