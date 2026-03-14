package com.stavre.cfrapiadapter.proxy;

import com.stavre.cfrapiadapter.config.OpenFeignConfiguration;
import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainTimeTable", url = "${cfr.base-url}", configuration = OpenFeignConfiguration.class)
public interface TrainTimeTableProxy {

    @GetMapping(value = "/ro-RO/Tren/{trainId}", produces = MediaType.TEXT_HTML_VALUE)
    String getTrainTokenPage(@PathVariable String trainId, @RequestParam(name = "Date") String date);

    @PostMapping(value = "/ro-RO/Trains/TrainsResult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getTrainTimeTable(@RequestBody RequestTrainTimeTableDto requestDetails);
}
