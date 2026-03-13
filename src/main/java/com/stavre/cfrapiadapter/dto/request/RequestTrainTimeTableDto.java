package com.stavre.cfrapiadapter.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
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

    //    TODO: Find a way to use record or POJO
}
