package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.response.StationDto;
import com.stavre.cfrapiadapter.service.StationService;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Station", description = "Station timetables and metadata")
public class StationController {

    private final StationService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/station/{stationName}")
    @Operation(summary = "Get station arrivals and departures",
            description = "Returns merged arrivals and departures for a station on the given date.")
    public List<EnrichedStationTrainDto> getDepartures(
            @PathVariable String stationName,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        return service.getStationTrains(stationName, date);
    }

    @GetMapping("/station/all")
    @Operation(summary = "Get all stations",
            description = "Returns the static list of all stations with their importance level.")
    public List<StationDto> getAllStations() {
        return service.getAllStations();
    }
}
