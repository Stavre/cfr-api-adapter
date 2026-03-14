package com.stavre.cfrapiadapter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RequestStationTrainsDto {
    @JsonProperty("Date")
    private String date;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("ReCaptcha")
    private String recaptcha;

    @JsonProperty("ConfirmationKey")
    private String confirmationKey;

    @JsonProperty("IsSearchWanted")
    private String isSearchWanted;

    @JsonProperty("IsReCaptchaFailed")
    private String isRecaptchaFailed;

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;
}
