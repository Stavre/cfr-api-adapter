package com.stavre.cfrapiadapter.dto.request;

import lombok.Data;

@Data
public class RequestStationTrainsDto {
    String Date;
    String StationName;
    String ReCaptcha;
    String ConfirmationKey;
    String IsSearchWanted;
    String IsReCaptchaFailed;
    String __RequestVerificationToken;

    public RequestStationTrainsDto(String date, String stationName, RequestDto requestDto) {
        this.Date = date;
        this.StationName = stationName;

        this.ReCaptcha = requestDto.ReCaptcha;
        this.ConfirmationKey = requestDto.ConfirmationKey;
        this.IsSearchWanted = requestDto.IsSearchWanted;
        this.IsReCaptchaFailed = requestDto.IsReCaptchaFailed;
        this.__RequestVerificationToken = requestDto.__RequestVerificationToken;


    }



//    TODO: Find a way to use record or POJO
}
