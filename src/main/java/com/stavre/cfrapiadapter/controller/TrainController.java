package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.adapter.TrainDelayResponseAdapter;
import com.stavre.cfrapiadapter.adapter.TrainStopAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
public class TrainController {

    private final TrainService service;
    private final TrainDelayResponseAdapter delayResponseAdapter = new TrainDelayResponseAdapter();

    @GetMapping("/train/{trainId}")
    public EnrichedTrainDto getTrainTimeTable(@PathVariable String trainId,
                                              @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        return service.getTrainStops(trainId, _date);
    }

    @GetMapping("/train/{trainId}/delay")
    public TrainDelayResponseDto getTrainDelay(@PathVariable String trainId,
                                               @RequestParam(required = false) String date,
                                               @RequestParam(required = false) String station) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        EnrichedTrainDto trainDto = service.getTrainStops(trainId, _date);
        if (station == null) {
            return delayResponseAdapter.adapt(trainDto, _date);
        }

        return delayResponseAdapter.adapt(trainDto, _date, station);
    }
//
//    @GetMapping("/trains")
//    public List<String> getAllTrainNumbers() {
//        return service.getAllTrains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//    }
}
