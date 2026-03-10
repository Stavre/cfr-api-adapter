package com.stavre.cfrapiadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CfrApiAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfrApiAdapterApplication.class, args);
    }

}
