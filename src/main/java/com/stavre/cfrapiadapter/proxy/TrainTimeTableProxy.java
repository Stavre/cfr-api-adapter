package com.stavre.cfrapiadapter.proxy;

import com.stavre.cfrapiadapter.config.OpenFeignConfiguration;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "trainTimeTable", url = "${cfr.base-url}", configuration = OpenFeignConfiguration.class)
public interface TrainTimeTableProxy {

    @GetMapping(value="/ro-RO/Tren/{trainId}", produces = MediaType.TEXT_HTML_VALUE)
    String getTrainTimeTable(@PathVariable String trainId, @RequestParam(name = "Date") String date);

    @PostMapping(value = "/ro-RO/Trains/TrainsResult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getTrainTimeTablePost(@RequestBody RequestTrainTimeTableDto requestDetails);
}
