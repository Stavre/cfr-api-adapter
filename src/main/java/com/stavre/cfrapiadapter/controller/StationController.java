package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataDto;
import com.stavre.cfrapiadapter.dto.request.metadata.RequestStationMetadataFactory;
import com.stavre.cfrapiadapter.dto.response.StationResponseDto;
import com.stavre.cfrapiadapter.dto.response.StationTotalDelayResponseDto;
import com.stavre.cfrapiadapter.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationController {

    private final RequestStationMetadataFactory metadataFactory;
    private final StationService service;

    @GetMapping("/station/{stationName}")
    public StationResponseDto getDepartures(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String inputDate
    ) {

        String date = inputDate == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : inputDate;

        var requestMetadataDto = metadataFactory.createDepartureRequestMetadata(date, stationName);
        List<EnrichedStationTrainDto> trainDepartures = service.getStationTrains(stationName, date);

        return new StationResponseDto(requestMetadataDto, trainDepartures);
    }

    @GetMapping("/station/delayed/{stationName}")
    public List<EnrichedStationTrainDto> getDelayedArrivals(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String input
    ) {
        String date = input == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : input;
        List<EnrichedStationTrainDto> arrivals = service.getStationTrains(stationName, date);

        return service.getDelayedTrains(arrivals);
    }

    @GetMapping("/station/delayed/total/{stationName}")
    public StationTotalDelayResponseDto getDelayedDeparturesTotal(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String inputDate
    ) {
        String date = inputDate == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : inputDate;

        var requestMetadataDto = metadataFactory.createDepartureRequestMetadata(date, stationName);

        List<EnrichedStationTrainDto> departures = service.getStationTrains(stationName, date);

        Duration totalDelay = service.getTotalDelay(departures);


        return new StationTotalDelayResponseDto(requestMetadataDto, totalDelay);

    }

//    @GetMapping("/station/departures/{stationName}")
//    public StationResponseDto getDepartures(@PathVariable String stationName, @RequestParam(required = false) String date) {
//
//        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
//
//        RequestStationMetadataDto requestMetadataDto = metadataFactory.createDepartureRequestMetadata(date, stationName);
//        List<EnrichedStationTrainDto> trainDepartures = service.getDepartures(stationName, _date);
//
//        return new StationResponseDto(requestMetadataDto, trainDepartures);
//    }
//
//
//    @GetMapping("/station/departures/delayed/{stationName}")
//    public List<EnrichedStationTrainDto> getDelayedArrivals(@PathVariable String stationName, @RequestParam(required = false) String date) {
//        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
//        List<EnrichedStationTrainDto> arrivals = service.getDepartures(stationName, _date);
//
//        return service.getDelayedTrains(arrivals);
//    }
//
//
//    @GetMapping("/station/departures/delayed/total/{stationName}")
//    public StationTotalDelayResponseDto getDelayedDeparturesTotal(@PathVariable String stationName, @RequestParam(required = false) String date) {
//        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
//
//        RequestStationMetadataDto requestMetadataDto = metadataFactory.createDepartureRequestMetadata(date, stationName);
//
//        List<EnrichedStationTrainDto> departures = service.getDepartures(stationName, _date);
//
//        Duration totalDelay = service.getTotalDelay(departures);
//
//
//        return new StationTotalDelayResponseDto(requestMetadataDto, totalDelay);
//
//    }
}
