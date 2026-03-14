package com.stavre.cfrapiadapter.dto.request.metadata;

import com.stavre.cfrapiadapter.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestTrainMetadataFactory {

    private final DateTimeUtils dateTimeUtils;

    public RequestTrainMetadataDto create(String input, String trainNumber) {
        String date = input == null ? dateTimeUtils.getCurrentDate() : input;
        return new RequestTrainMetadataDto(date, trainNumber);
    }
}
