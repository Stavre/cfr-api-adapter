package com.stavre.cfrapiadapter.exception.train;

public class TrainNotFoundException extends RuntimeException {
    public TrainNotFoundException(String trainNumber) {
        super("Train %s not found".formatted(trainNumber));
    }
}
