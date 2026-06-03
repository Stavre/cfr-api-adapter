package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.response.DelayDto;
import com.stavre.cfrapiadapter.service.StationService;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/station/arrivals")
@Tag(name = "Station Arrivals", description = "Delayed arrival queries")
public class StationArrivalsController {
    private final StationService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/delayed/{stationName}")
    @Operation(summary = "Get delayed arrivals",
            description = "Returns only delayed arrivals for a station on the given date.")
    public List<EnrichedStationTrainDto> getDelayedArrivals(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);
        List<EnrichedStationTrainDto> arrivals = service.getStationTrains(stationName, date);

        return service.getDelayedArrivals(arrivals);
    }

    @GetMapping("/delayed/total/{stationName}")
    @Operation(summary = "Get total arrivals delay",
            description = "Returns the aggregated total delay across all delayed arrivals for a station.")
    public DelayDto getDelayedDeparturesTotal(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        List<EnrichedStationTrainDto> arrivals = service.getStationTrains(stationName, date);
        List<EnrichedStationTrainDto> delayedArrivals = service.getDelayedArrivals(arrivals);

        return new DelayDto(service.getTotalArrivalsDelay(delayedArrivals));
    }
}
