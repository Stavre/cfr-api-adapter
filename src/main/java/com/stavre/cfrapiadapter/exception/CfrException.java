package com.stavre.cfrapiadapter.exception;

import lombok.Getter;
import java.io.Serial;
import java.util.List;

public class CfrException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    List<String> errors;

    public CfrException(List<String> errors) {
        super("CFR deems your input invalid");
        this.errors = errors;
    }
}
