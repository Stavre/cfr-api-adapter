package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.service.TrainService;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TrainController {

    private final TrainService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/train/{trainNumber}")
    public EnrichedTrainDto getTrainTimeTable(@PathVariable String trainNumber,
                                              @RequestParam(required = false) String date) {

        return service.getTrainStops(trainNumber, date);
    }

    @GetMapping("/train/{trainId}/delay")
    public TrainDelayResponseDto getTrainDelay(@PathVariable String trainId,
                                               @RequestParam(value = "date", required = false) String inputDate,
                                               @RequestParam(required = false) String station) {
        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        EnrichedTrainDto trainDto = service.getTrainStops(trainId, date);
        if (station == null) {
            return service.getTrainDelay(trainDto);
        }

        return service.getTrainDelay(trainDto, station);
    }

    @GetMapping("/train/all")
    public List<String> getAllTrains() {
        return service.getAllTrains();
    }
}