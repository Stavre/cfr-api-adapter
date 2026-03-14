package com.stavre.cfrapiadapter.dto.request.metadata;

import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestStationMetadataFactory {

    private final DateTimeUtils dateTimeUtils;

    public RequestStationMetadataDto createDepartureRequestMetadata(String inputDate, String station) {
        String date = inputDate == null ? dateTimeUtils.getCurrentDate() : inputDate;
        return new RequestStationMetadataDto(date, station);
    }
}
