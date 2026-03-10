package com.stavre.cfrapiadapter.proxy;

import com.stavre.cfrapiadapter.config.OpenFeignConfiguration;
import com.stavre.cfrapiadapter.dto.request.RequestStationTrainsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "station", url = "${cfr.base-url}", configuration = OpenFeignConfiguration.class)
public interface TrainStationProxy {

    @GetMapping(value="/ro-RO/Statie/{stationName}", produces = MediaType.TEXT_HTML_VALUE)
    String getStationTrains(@PathVariable String stationName, @RequestParam(name = "Date") String date);

    @PostMapping(value = "/ro-RO/Stations/StationsResult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getStationTrainsPost(@RequestBody RequestStationTrainsDto requestDetails);
}
