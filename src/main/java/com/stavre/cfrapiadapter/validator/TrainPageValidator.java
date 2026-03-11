package com.stavre.cfrapiadapter.validator;

import com.stavre.cfrapiadapter.exception.train.InvalidDateException;
import com.stavre.cfrapiadapter.exception.train.TrainNotFoundException;
import com.stavre.cfrapiadapter.exception.train.TrainNotPresentOnDateException;
import lombok.NonNull;

public class TrainPageValidator {

    public void validate(@NonNull String htmlPage, String trainNumber, String date) {
        if (trainMissing(htmlPage)) {
            throw new TrainNotFoundException(trainNumber);
        }

        if (trainMissingOnDate(htmlPage)) {
            throw new TrainNotPresentOnDateException(trainNumber, date);
        }

        if (requestOutsideOfTimeInterval(htmlPage)) {
            throw new InvalidDateException(30);
        }
    }

    private boolean trainMissing(String htmlPage) {
        return htmlPage.contains("Nu a fost găsit niciun tren cu acest număr!");
    }

    private boolean trainMissingOnDate(String htmlPage) {
        return htmlPage.contains("nu circulă în data de");
    }

    private boolean requestOutsideOfTimeInterval(String htmlPage) {
        return htmlPage.contains("Data nu se află în intervalul de 30 de zile!");
    }
}
