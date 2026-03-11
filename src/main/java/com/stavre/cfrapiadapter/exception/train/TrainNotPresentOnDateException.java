package com.stavre.cfrapiadapter.exception.train;

public class TrainNotPresentOnDateException extends RuntimeException {
    public TrainNotPresentOnDateException(String trainNumber, String date) {
        super("Train %s not scheduled on %s".formatted(trainNumber, date));
    }
}
