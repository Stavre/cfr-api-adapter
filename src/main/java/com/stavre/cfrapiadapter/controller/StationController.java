package com.stavre.cfrapiadapter.controller;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedStationTrainDto;
import com.stavre.cfrapiadapter.dto.response.StationDto;
import com.stavre.cfrapiadapter.service.StationService;
import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationController {

    private final StationService service;
    private final DateTimeUtils dateTimeUtils;

    @GetMapping("/station/{stationName}")
    public List<EnrichedStationTrainDto> getDepartures(
            @PathVariable String stationName,
            @RequestParam(value = "date", required = false) String inputDate) {

        String date = dateTimeUtils.getDateOrGetToday(inputDate);

        return service.getStationTrains(stationName, date);
    }

    @GetMapping("/station/all")
    public List<StationDto> getAllStations() {
        return service.getAllStations();
    }
}
