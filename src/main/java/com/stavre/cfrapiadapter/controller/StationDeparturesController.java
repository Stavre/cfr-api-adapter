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
@RequestMapping(value = "/station/departures")
@Tag(name = "Station Departures", description = "Delayed departure queries")
public class StationDeparturesController {
    private final StationService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/{stationName}")
    @Operation(summary = "Get all departures",
            description = "Returns all departures for a station on the given date.")
    public List<EnrichedStationTrainDto> getDepartures(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);
        List<EnrichedStationTrainDto> trains = service.getStationTrains(stationName, date);
        return service.getDepartures(trains);
    }

    @GetMapping("/delayed/{stationName}")
    @Operation(summary = "Get delayed departures",
            description = "Returns only delayed departures for a station on the given date.")
    public List<EnrichedStationTrainDto> getDelayedDepartures(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);
        List<EnrichedStationTrainDto> departures = service.getStationTrains(stationName, date);

        return service.getDelayedDepartures(departures);
    }

    @GetMapping("/delayed/total/{stationName}")
    @Operation(summary = "Get total departures delay",
            description = "Returns the aggregated total delay across all delayed departures for a station.")
    public DelayDto getDelayedDeparturesTotal(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        List<EnrichedStationTrainDto> departures = service.getStationTrains(stationName, date);
        List<EnrichedStationTrainDto> delayedDepartures = service.getDelayedDepartures(departures);

        return new DelayDto(service.getTotalDeparturesDelay(delayedDepartures));
    }
}
