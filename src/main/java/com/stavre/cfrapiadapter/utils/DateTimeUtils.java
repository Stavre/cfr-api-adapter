package com.stavre.cfrapiadapter.utils;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Optional;

@Component
public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().format(formatter);
    }

    public Optional<LocalTime> convertTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            return Optional.of(LocalTime.parse(time.trim(), formatter));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDate> convertDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return Optional.of(LocalDate.parse(date.trim(), formatter));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getDateOrGetToday(String inputDate) {
        return inputDate == null ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : inputDate;
    }
}
