package com.stavre.cfrapiadapter.dto.request.metadata;

import com.stavre.cfrapiadapter.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestTrainMetadataFactory {

    private final DateUtils dateUtils;

    public RequestTrainMetadataDto create(String date, String trainNumber) {
        String _date = date == null ? dateUtils.getCurrentDate() : date;
        return new RequestTrainMetadataDto(_date, trainNumber);
    }
}
