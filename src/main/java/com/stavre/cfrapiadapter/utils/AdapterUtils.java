package com.stavre.cfrapiadapter.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class AdapterUtils {

    private final DateTimeUtils dateTimeUtils;

    public String getTrainPlatform(String platform, List<String> errors) {
        if (platform == null || platform.isBlank()) {
            errors.add("Platform string is null or blank.");
            return null;
        }

        String regex = "^linia\\s+(.+)$";
        var pattern = java.util.regex.Pattern.compile(regex); // no CASE_INSENSITIVE
        var matcher = pattern.matcher(platform.trim());

        if (!matcher.matches()) {
            errors.add("Platform string does not match expected format: " + platform);
            return null;
        }

        return matcher.group(1).trim();
    }

    public LocalDateTime getTimestamp(String date, String time, List<String> errors) {

        Optional<LocalDate> dateOpt = dateTimeUtils.convertDate(date);
        if (dateOpt.isEmpty()) {
            errors.add("Could not convert date %s to date object".formatted(date));
            errors.add("Could not compute timestamp");
            return null;
        }

        Optional<LocalTime> timeOpt = dateTimeUtils.convertTime(time);
        if (timeOpt.isEmpty()) {
            errors.add("Could not convert time %s to time object".formatted(time));
            errors.add("Could not compute timestamp");
            return null;
        }

        return dateOpt.flatMap(d ->
                timeOpt.map(d::atTime)
        ).orElse(null);
    }

    public Duration getDelay(String input, List<String> errors) {
        if (input == null || input.isBlank()) {
            errors.add("Delay string is null or blank");
            return null;
        }

        String trimmed = input.trim();

        // Case 1: "la timp" or "la timp*"
        if (trimmed.matches("^la timp\\*?$")) {
            return Duration.ofMinutes(0);
        }

        // Case 2: "+7 min (întârziere)" or "+7 min (întârziere)*"
        // Mandatory space before "min"
        Pattern pattern = Pattern.compile("^\\+(\\d+) min.*$");
        Matcher matcher = pattern.matcher(trimmed);

        if (matcher.matches()) {
            int minutes = Integer.parseInt(matcher.group(1));
            return Duration.ofMinutes(minutes);
        }

        errors.add("Could not extract delay from string: " + input);
        return null;
    }

    public List<String> getDirection(String mainStations, List<String> errors) {

        if (mainStations == null || mainStations.isBlank()) {
            errors.add("No main stations found");
            return List.of();
        }

        return Arrays.stream(mainStations.split("-"))
                .map(String::trim)
                .toList();
    }

    public Duration getStopDuration(String duration, List<String> errors) {
        try {
            if (duration.contains("necunoscută")) {
                return null;
            }
            int durationAsInt = Integer.parseInt(duration.split(" ")[0]);
            return Duration.ofMinutes(durationAsInt);
        } catch (Exception e) {
            errors.add("Could not convert label %s into duration".formatted(duration));
            return null;
        }
    }
}