package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.service.TrainService;
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
@Tag(name = "Train", description = "Train timetables and delays")
public class TrainController {

    private final TrainService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/train/{trainNumber}")
    @Operation(summary = "Get train timetable",
            description = "Returns all stops for a train on the given date, grouped by branch.")
    public EnrichedTrainDto getTrainTimeTable(
            @PathVariable String trainNumber,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);
        return service.getTrainStops(trainNumber, date);
    }

    @GetMapping("/train/{trainId}/delay")
    @Operation(summary = "Get train delay",
            description = "Returns delay per branch. Omitting station returns delay at each branch's last stop.")
    public TrainDelayResponseDto getTrainDelay(
            @PathVariable String trainId,
            @Parameter(description = "Date in dd.MM.yyyy format. Defaults to today if omitted.")
            @RequestParam(value = "date", required = false) String inputDate,
            @Parameter(description = "Filter delay to a specific station name.")
            @RequestParam(required = false) String station) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        EnrichedTrainDto trainDto = service.getTrainStops(trainId, date);
        if (station == null) {
            return service.getTrainDelay(trainDto);
        }

        return service.getTrainDelay(trainDto, station);
    }

    @GetMapping("/train/all")
    @Operation(summary = "Get all train numbers",
            description = "Returns the static list of all known train numbers.")
    public List<String> getAllTrains() {
        return service.getAllTrains();
    }
}