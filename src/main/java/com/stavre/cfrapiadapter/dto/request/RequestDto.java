package com.stavre.cfrapiadapter.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestDto {
    String ReCaptcha;
    String ConfirmationKey;
    String IsSearchWanted;
    String IsReCaptchaFailed;
    String __RequestVerificationToken;

    public RequestDto(RequestDto requestDto) {
        this.ReCaptcha = requestDto.ReCaptcha;
        this.ConfirmationKey = requestDto.ConfirmationKey;
        this.IsSearchWanted = requestDto.IsSearchWanted;
        this.IsReCaptchaFailed = requestDto.IsReCaptchaFailed;
        this.__RequestVerificationToken = requestDto.__RequestVerificationToken;
    }
}
