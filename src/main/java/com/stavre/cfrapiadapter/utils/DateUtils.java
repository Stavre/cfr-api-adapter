package com.stavre.cfrapiadapter.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

@Component
public class DateUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().format(formatter);
    }


//    public String dateToString(LocalDate date) {
//
//    }
}
