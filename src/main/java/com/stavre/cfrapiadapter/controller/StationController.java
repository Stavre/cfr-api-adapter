package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.train.TrainDepartureDto;
import com.stavre.cfrapiadapter.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
public class StationController {

    private final StationService service;

    @GetMapping("/station/{stationName}")
    public List<EnrichedTrainDepartureDto> getTrainTimeTable(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        return service.getDepartures(stationName, _date);
    }

    @GetMapping("/stations")
    public List<String> getAllStations() {
        return service.getAllStations(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}
