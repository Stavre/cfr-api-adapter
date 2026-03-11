package com.stavre.cfrapiadapter.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdapterUtils {

    public String getTrainPlatform(String platform, List<String> errors) {
        if (platform.isBlank()) {
            errors.add("Train platform is blank");
            return null;
        }

        return platform.replace("linia", "").trim();
    }

    public LocalDateTime getDepartureTimestamp(String date, String time, List<String> errors) {
        LocalDateTime arrivalTimestamp = null;

        LocalDate convertedDate = null;

        Optional<LocalDate> convertedDateOpt = convertDate(date);

        if (convertedDateOpt.isEmpty()) {
            errors.add("Could not convert departure date %s to date object".formatted(date));
        } else {
            convertedDate = convertedDateOpt.get();
        }

        LocalTime convertedArrivalTime = null;
        Optional<LocalTime> convertedArrivalTimeOpt = convertTime(time);

        if (convertedArrivalTimeOpt.isEmpty()) {
            errors.add("Could not convert departure time %s to time object".formatted(time));
        } else {
            convertedArrivalTime = convertedArrivalTimeOpt.get();
        }

        if (convertedDateOpt.isPresent() && convertedArrivalTimeOpt.isPresent()) {
            arrivalTimestamp = convertedDate.atTime(convertedArrivalTime);
        } else {
            errors.add("Could not compute departure timestamp");
        }

        return arrivalTimestamp;
    }

    public LocalDateTime getArrivalTimestamp(String date, String time, List<String> errors) {
        LocalDateTime arrivalTimestamp = null;

        LocalDate convertedDate = null;

        Optional<LocalDate> convertedDateOpt = convertDate(date);

        if (convertedDateOpt.isEmpty()) {
            errors.add("Could not convert arrival date %s to date object".formatted(date));
        } else {
            convertedDate = convertedDateOpt.get();
        }

        LocalTime convertedArrivalTime = null;
        Optional<LocalTime> convertedArrivalTimeOpt = convertTime(time);

        if (convertedArrivalTimeOpt.isEmpty()) {
            errors.add("Could not convert arrival time %s to time object".formatted(time));
        } else {
            convertedArrivalTime = convertedArrivalTimeOpt.get();
        }

        if (convertedDateOpt.isPresent() && convertedArrivalTimeOpt.isPresent()) {
            arrivalTimestamp = convertedDate.atTime(convertedArrivalTime);
        } else {
            errors.add("Could not compute arrival timestamp");
        }

        return arrivalTimestamp;
    }

    public Optional<LocalTime> convertTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            return Optional.of(LocalTime.parse(time, formatter));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDate> convertDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return Optional.of(LocalDate.parse(date, formatter));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Duration getDelay(String delay, List<String> errors) {
        try {
            if (delay.contains("la timp") || delay.isBlank()) {
                return Duration.ofMinutes(0);
            }

            String durationInMinutes = delay.replace("*", "");

            // Extract the number before "min"
            String minutesPart = durationInMinutes.split("min")[0].trim();

            // Remove any leading "+" or other symbols
            minutesPart = minutesPart.replace("+", "").trim();

            int minutes = Integer.parseInt(minutesPart);

            return Duration.ofMinutes(minutes);
        } catch (Exception e) {
            errors.add("Could not convert label %s into Duration".formatted(delay));
            return null;
        }

    }
}
