package com.stavre.cfrapiadapter.exception.train;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(int interval) {
        super("Date outside interval of %s days".formatted(interval));
    }
}
