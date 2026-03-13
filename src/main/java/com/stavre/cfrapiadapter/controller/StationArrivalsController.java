package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataFactory;
import com.stavre.cfrapiadapter.dto.response.StationDelayResponseDto;
import com.stavre.cfrapiadapter.dto.response.StationResponseDto;
import com.stavre.cfrapiadapter.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationArrivalsController {

    private final RequestStationMetadataFactory metadataFactory;

    private final StationService service;

    @GetMapping("/station/arrivals/{stationName}")
    public StationResponseDto getArrivals(@PathVariable String stationName, @RequestParam(required = false) String date) {

        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        RequestStationMetadataDto requestMetadataDto = metadataFactory.createArrivalRequestMetadata(date, stationName);
        List<EnrichedStationTrainDto> trainArrivals = service.getArrivals(stationName, _date);

        return new StationResponseDto(requestMetadataDto, trainArrivals);
    }

    @GetMapping("/station/arrivals/delayed/{stationName}")
    public List<EnrichedStationTrainDto> getDelayedArrivals(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        List<EnrichedStationTrainDto> arrivals = service.getArrivals(stationName, _date);

        return service.getDelayedTrains(arrivals);
    }

    @GetMapping("/station/arrivals/delayed/total/{stationName}")
    public StationDelayResponseDto getDelayedArrivalsTotal(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;

        List<EnrichedStationTrainDto> arrivals = service.getArrivals(stationName, _date);

        Duration totalDelay = service.getTotalDelay(arrivals);

        return new StationDelayResponseDto(
                LocalDateTime.now(),
                _date,
                stationName,
                totalDelay
        );
    }
}
