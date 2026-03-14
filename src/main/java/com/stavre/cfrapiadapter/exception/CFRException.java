package com.stavre.cfrapiadapter.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.List;

public class CFRException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    List<String> errors;

    public CFRException(List<String> errors) {
        super("CFR deems your input invalid");
        this.errors = errors;
    }
}
