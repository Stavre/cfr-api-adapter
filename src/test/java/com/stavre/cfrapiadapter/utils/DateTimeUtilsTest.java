package com.stavre.cfrapiadapter.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeUtilsTest {

    private final DateTimeUtils converter = new DateTimeUtils();

    @Test
    void shouldReturnEmptyOptional_WhenInputIsNull() {
        Optional<LocalTime> result = converter.convertTime(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_WhenInputIsBlank() {
        Optional<LocalTime> result = converter.convertTime("   ");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_WhenInputIsInvalidFormat() {
        Optional<LocalTime> result = converter.convertTime("abc");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_WhenMinutesAreInvalid() {
        Optional<LocalTime> result = converter.convertTime("10:99");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_WhenHoursAreInvalid() {
        Optional<LocalTime> result = converter.convertTime("25:10");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldParseValidTime() {
        Optional<LocalTime> result = converter.convertTime("9:05");

        assertThat(result)
                .isPresent()
                .contains(LocalTime.of(9, 5));
    }

    @Test
    void shouldParseValidTimeWithTwoDigitHour() {
        Optional<LocalTime> result = converter.convertTime("14:30");

        assertThat(result)
                .isPresent()
                .contains(LocalTime.of(14, 30));
    }

    @Test
    void shouldReturnEmptyOptional_whenInputIsNull() {
        Optional<LocalDate> result = converter.convertDate(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenInputIsBlank() {
        Optional<LocalDate> result = converter.convertDate("   ");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenFormatIsInvalid() {
        Optional<LocalDate> result = converter.convertDate("2024-01-10");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenDayIsInvalid() {
        Optional<LocalDate> result = converter.convertDate("32.01.2024");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenMonthIsInvalid() {
        Optional<LocalDate> result = converter.convertDate("10.13.2024");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenYearIsInvalid() {
        Optional<LocalDate> result = converter.convertDate("10.01.20");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldParseValidDate() {
        Optional<LocalDate> result = converter.convertDate("10.01.2024");

        assertThat(result)
                .isPresent()
                .contains(LocalDate.of(2024, 1, 10));
    }

    @Test
    void shouldParseValidDate_withLeadingAndTrailingSpaces() {
        Optional<LocalDate> result = converter.convertDate("   05.12.2023   ");

        assertThat(result)
                .isPresent()
                .contains(LocalDate.of(2023, 12, 5));
    }
}
