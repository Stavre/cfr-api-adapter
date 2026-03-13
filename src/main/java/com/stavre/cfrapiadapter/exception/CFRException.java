package com.stavre.cfrapiadapter.exception;

import lombok.Getter;

import java.util.List;

public class CFRException extends RuntimeException {

    @Getter
    List<String> errors;

    public CFRException(List<String> errors) {
        super("CFR deems your input invalid");
        this.errors = errors;
    }
}
