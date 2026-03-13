package com.stavre.cfrapiadapter.exception;

import java.util.List;

public record CFRExceptionDto(
        String message,
        List<String> errors
) {
}
