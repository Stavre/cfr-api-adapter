package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.TrainStopDto;
import com.stavre.cfrapiadapter.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TrainTimeTableController {

    private final TrainService service;

    @GetMapping("/train/{trainId}")
    public List<TrainStopDto> getTrainTimeTable(@PathVariable String trainId, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        return service.getTrainStops(trainId, _date);
    }

    @GetMapping("/trains")
    public List<String> getAllTrainNumbers() {
        return service.getAllTrains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}
