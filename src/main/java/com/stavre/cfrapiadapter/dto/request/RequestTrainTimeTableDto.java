package com.stavre.cfrapiadapter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RequestTrainTimeTableDto {
    @JsonProperty("Date")
    private String date;

    @JsonProperty("TrainRunningNumber")
    private String trainRunningNumber;

    @JsonProperty("SelectedBranchCode")
    private String selectedBranchCode;

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
