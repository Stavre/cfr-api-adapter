package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.adapter.TrainDelayResponseAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestTrainMetadataFactory;
import com.stavre.cfrapiadapter.dto.response.TrainDelayResponseDto;
import com.stavre.cfrapiadapter.dto.response.TrainResponseDto;
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

    private final RequestTrainMetadataFactory requestTrainMetadataFactory;

    private final TrainService service;
    private final TrainDelayResponseAdapter delayResponseAdapter = new TrainDelayResponseAdapter();

    @GetMapping("/train/{trainNumber}")
    public TrainResponseDto getTrainTimeTable(@PathVariable String trainNumber,
                                              @RequestParam(required = false) String date) {

        RequestTrainMetadataDto requestMetadata = requestTrainMetadataFactory.create(date, trainNumber);
        EnrichedTrainDto train = service.getTrainStops(trainNumber, date);
        return new TrainResponseDto(requestMetadata, train);
    }

    @GetMapping("/train/{trainId}/delay")
    public TrainDelayResponseDto getTrainDelay(@PathVariable String trainId,
                                               @RequestParam(value = "date", required = false) String inputDate,
                                               @RequestParam(required = false) String station) {
        String date = inputDate == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : inputDate;

        EnrichedTrainDto trainDto = service.getTrainStops(trainId, date);
        if (station == null) {
            return delayResponseAdapter.adapt(trainDto, date);
        }

        return delayResponseAdapter.adapt(trainDto, date, station);
    }
}