package com.stavre.cfrapiadapter.dto.request.metadata;

import com.stavre.cfrapiadapter.dto.enriched.StationTrainType;
import com.stavre.cfrapiadapter.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestStationMetadataFactory {

    private final DateUtils dateUtils;

    public RequestStationMetadataDto createDepartureRequestMetadata(String date, String station) {
        String _date = date == null ? dateUtils.getCurrentDate() : date;
        return new RequestStationMetadataDto(_date, station, StationTrainType.DEPARTURE);
    }

    public RequestStationMetadataDto createArrivalRequestMetadata(String date, String station) {
        String _date = date == null ? dateUtils.getCurrentDate() : date;
        return new RequestStationMetadataDto(_date, station, StationTrainType.ARRIVAL);
    }
}
