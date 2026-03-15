package com.stavre.cfrapiadapter.exception;

import java.util.List;

public record CfrExceptionDto(
        String message,
        List<String> errors
) {}
