package com.stavre.cfrapiadapter.dto.request;

import lombok.Data;

@Data
public class RequestTrainTimeTableDto {
    String Date;
    String TrainRunningNumber;
    String SelectedBranchCode;
    String ReCaptcha;
    String ConfirmationKey;
    String IsSearchWanted;
    String IsReCaptchaFailed;
    String __RequestVerificationToken;

    public RequestTrainTimeTableDto(String date, String trainRunningNumber, String selectedBranchCode, RequestDto requestDto) {
        this.Date = date;
        this.TrainRunningNumber = trainRunningNumber;
        this.SelectedBranchCode = selectedBranchCode;
        this.ReCaptcha = requestDto.ReCaptcha;
        this.ConfirmationKey = requestDto.ConfirmationKey;
        this.IsSearchWanted = requestDto.IsSearchWanted;
        this.IsReCaptchaFailed = requestDto.IsReCaptchaFailed;
        this.__RequestVerificationToken = requestDto.__RequestVerificationToken;

    }

//    TODO: Find a way to use record or POJO
}
