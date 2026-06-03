package com.stavre.cfrapiadapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CFR API Adapter")
                        .description("REST API that scrapes the Romanian Railway Company (CFR) website"
                                + " and exposes train and station data.")
                        .version("0.0.1"));
    }
}
