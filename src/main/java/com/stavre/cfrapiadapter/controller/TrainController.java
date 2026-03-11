package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.adapter.TrainStopAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainStopDto;
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
public class TrainController {

    private final TrainService service;
    private final TrainStopAdapter adapter = new TrainStopAdapter();

    @GetMapping("/train/{trainId}")
    public List<EnrichedTrainStopDto> getTrainTimeTable(@PathVariable String trainId, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        return service.getTrainStops(trainId, _date).stream()
                .map(s -> adapter.adapt(s, _date))
                .toList();
    }

    @GetMapping("/trains")
    public List<String> getAllTrainNumbers() {
        return service.getAllTrains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}
