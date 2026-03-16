package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.response.DelayDto;
import com.stavre.cfrapiadapter.service.StationService;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/station/arrivals")
public class StationArrivalsController {
    private final StationService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/delayed/{stationName}")
    public List<EnrichedStationTrainDto> getDelayedArrivals(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String inputDate
    ) {
        String date = dateTimeUtils.getDateOrGetToday(inputDate);
        List<EnrichedStationTrainDto> arrivals = service.getStationTrains(stationName, date);

        return service.getDelayedArrivals(arrivals);
    }

    @GetMapping("/delayed/total/{stationName}")
    public DelayDto getDelayedDeparturesTotal(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String inputDate
    ) {
        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        List<EnrichedStationTrainDto> arrivals = service.getStationTrains(stationName, date);
        List<EnrichedStationTrainDto> delayedArrivals = service.getDelayedArrivals(arrivals);

        return new DelayDto(service.getTotalArrivalsDelay(delayedArrivals));
    }
}
