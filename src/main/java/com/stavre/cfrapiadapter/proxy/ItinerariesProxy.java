package com.stavre.cfrapiadapter.proxy;

import com.stavre.cfrapiadapter.config.OpenFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
@FeignClient(name = "itineraries", url = "${cfr.base-url}", configuration = OpenFeignConfiguration.class)
public interface ItinerariesProxy {

    @GetMapping(value = "/ro-RO/Itineraries", produces = MediaType.TEXT_HTML_VALUE)
    String getItinerariesPage();
}
