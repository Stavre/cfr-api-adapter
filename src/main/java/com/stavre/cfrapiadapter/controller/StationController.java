package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainArrivalDto;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDepartureDto;
import com.stavre.cfrapiadapter.dto.response.StationDelayResponseDto;
import com.stavre.cfrapiadapter.dto.train.TrainDepartureDto;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;

@RequiredArgsConstructor
@RestController
public class StationController {

    private final StationService service;

    @GetMapping("/station/departures/{stationName}")
    public List<EnrichedTrainDepartureDto> getDepartures(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        return service.getDepartures(stationName, _date);
    }

    @GetMapping("/station/arrivals/{stationName}")
    public List<EnrichedTrainArrivalDto> getArrivals(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        return service.getArrivals(stationName, _date);
    }

    @GetMapping("/station/arrivals/delayed/{stationName}")
    public List<EnrichedTrainArrivalDto> getDelayedArrivals(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        return service.getArrivals(stationName, _date)
                .parallelStream()
                .filter(arrival -> !arrival.arrivalDelay().equals(Duration.ofMinutes(0)))
                .toList();
    }

    @GetMapping("/station/arrivals/delayed/total/{stationName}")
    public StationDelayResponseDto getDelayedArrivalsTotal(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        Duration totalDelay = service.getArrivals(stationName, _date)
                .parallelStream()
                .map(EnrichedTrainArrivalDto::arrivalDelay)
                .filter(delay -> !delay.equals(Duration.ofMinutes(0)))
                .reduce(Duration.ofMinutes(0), Duration::plus);

        return new StationDelayResponseDto(
                LocalDateTime.now(),
                _date,
                stationName,
                totalDelay
        );
    }

    @GetMapping("/station/departures/delayed/total/{stationName}")
    public StationDelayResponseDto getDelayedDeparturesTotal(@PathVariable String stationName, @RequestParam(required = false) String date) {
        String _date = date == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : date;
        Duration totalDelay = service.getDepartures(stationName, _date)
                .parallelStream()
                .map(EnrichedTrainDepartureDto::departureDelay)
                .filter(delay -> !delay.equals(Duration.ofMinutes(0)))
                .reduce(Duration.ofMinutes(0), Duration::plus);

        return new StationDelayResponseDto(
                LocalDateTime.now(),
                _date,
                stationName,
                totalDelay
        );
    }

//    @GetMapping("/stations")
//    public List<String> getAllStations() {
//        return service.getAllStations(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//    }
}
