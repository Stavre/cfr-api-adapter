package com.stavre.cfrapiadapter.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdapterUtilsTest {

    public static final String DATE = "10.01.2024";
    @Mock
    private DateTimeUtils dateTimeUtils;

    @InjectMocks
    private AdapterUtils utils;

    private static final String PLATFORM_STRING_IS_NULL_OR_BLANK = "Platform string is null or blank.";

    @Test
    void shouldReturnNullAndAddError_WhenInputIsNull() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform(null, errors);

        assertThat(result).isNull();
        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void shouldReturnNullAndAddError_WhenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("   ", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void shouldReturnNullAndAddError_WhenFormatDoesNotMatch() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("Linia 4A", errors); // wrong case

        assertThat(result).isNull();
        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo("Platform string does not match expected format: Linia 4A");
    }

    @Test
    void shouldExtractPlatform_WhenFormatIsValid() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("linia 4A", errors);

        assertThat(result).isEqualTo("4A");
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractPlatform_WhenExtraSpacesArePresent() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("  linia    X123   ", errors);

        assertThat(result).isEqualTo("X123");
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNullAndAddError_WhenNothingAfterLinia() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("linia ", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo("Platform string does not match expected format: linia ");
    }


    @Test
    void shouldReturnNullAndAddErrors_whenDateCannotBeConverted() {
        List<String> errors = new ArrayList<>();

        when(dateTimeUtils.convertDate("bad-date")).thenReturn(Optional.empty());

        LocalDateTime result = utils.getTimestamp("bad-date", "10:30", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly(
                        "Could not convert date bad-date to date object",
                        "Could not compute timestamp"
                );

        verify(dateTimeUtils).convertDate("bad-date");
        verify(dateTimeUtils, never()).convertTime(anyString());
    }

    @Test
    void shouldReturnNullAndAddErrors_whenTimeCannotBeConverted() {
        List<String> errors = new ArrayList<>();

        when(dateTimeUtils.convertDate(DATE))
                .thenReturn(Optional.of(LocalDate.of(2024, 1, 10)));
        when(dateTimeUtils.convertTime("bad-time"))
                .thenReturn(Optional.empty());

        LocalDateTime result = utils.getTimestamp(DATE, "bad-time", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly(
                        "Could not convert time bad-time to time object",
                        "Could not compute timestamp"
                );

        verify(dateTimeUtils).convertDate(DATE);
        verify(dateTimeUtils).convertTime("bad-time");
    }

    @Test
    void shouldReturnTimestamp_whenDateAndTimeAreValid() {
        List<String> errors = new ArrayList<>();

        LocalDate date = LocalDate.of(2024, 2, 10);
        LocalTime time = LocalTime.of(14, 30);

        when(dateTimeUtils.convertDate("10.02.2024")).thenReturn(Optional.of(date));
        when(dateTimeUtils.convertTime("14:30")).thenReturn(Optional.of(time));

        LocalDateTime result = utils.getTimestamp("10.02.2024", "14:30", errors);

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 2, 10, 14, 30));
        assertThat(errors).isEmpty();

        verify(dateTimeUtils).convertDate("10.02.2024");
        verify(dateTimeUtils).convertTime("14:30");
    }


    @Test
    void shouldReturnNullAndAddError_whenInputIsNull() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay(null, errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly("Delay string is null or blank");
    }

    @Test
    void shouldReturnNullAndAddError_whenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("  ", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly("Delay string is null or blank");
    }

    @Test
    void shouldReturnZeroDuration_whenInputIsLaTimp() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("la timp", errors);

        assertThat(result).isEqualTo(Duration.ZERO);
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnZeroDuration_whenInputIsLaTimpWithAsterisk() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("la timp*", errors);

        assertThat(result).isEqualTo(Duration.ZERO);
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractDelay_whenValidDelayString() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+7 min (întârziere)", errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(7));
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractDelay_whenValidDelayStringWithAsterisk() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+3 min (întârziere)*", errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(3));
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNullAndAddError_whenMissingSpaceBeforeMin() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+5min", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly("Could not extract delay from string: +5min");
    }

    @Test
    void shouldReturnNullAndAddError_whenInvalidFormat() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("random text", errors);

        assertThat(result).isNull();
        assertThat(errors)
                .containsExactly("Could not extract delay from string: random text");
    }

    @Test
    void getTrainPlatform_withValidPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia 2";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isEqualTo("2");
        verifyNoInteractions(errors);
    }

    @Test
    void getTrainPlatform_withValidComplexPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia 2A";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isEqualTo("2A");
        verifyNoInteractions(errors);
    }

    @Test
    void getTrainPlatform_withNullPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = null;
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
        verify(errors).add(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void getTrainPlatform_withBlankPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = " ";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
        verify(errors).add(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void getTrainPlatform_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia2";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
        verify(errors).add("Platform string does not match expected format: linia2");
    }

    @Test
    void getTimestamp_withValidDateAndTime() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = DATE;
        String time = "19:30";

        LocalDateTime result = utils.getTimestamp(date, time, mock(List.class));

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 1, 10, 19, 30));
    }

    @Test
    void getTimestamp_withInvalidDate() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "invalid-date";
        String time = "19:30";

        LocalDateTime result = utils.getTimestamp(date, time, mock(List.class));

        assertThat(result).isNull();
        verifyNoInteractions(mock(List.class));
    }

    @Test
    void getTimestamp_withInvalidTime() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "2024-01-10";
        String time = "invalid-time";

        LocalDateTime result = utils.getTimestamp(date, time, mock(List.class));

        assertThat(result).isNull();
        verifyNoInteractions(mock(List.class));
    }

    @Test
    void getDelay_withValidDelay() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "+15 min (întârziere)*";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void getDelay_withValidButEmptyInput() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isNull();
        verify(errors).add("Delay string is null or blank");
    }

    @Test
    void getDelay_withValidNoDuration() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "la timp*";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(0));
    }

    @Test
    void getDelay_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "invalid-format";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isNull();
        verify(errors).add("Could not extract delay from string: invalid-format");
    }

    @Test
    void getDirection_withValidMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = "stationA-stationB";
        List<String> errors = mock(List.class);

        List<String> result = utils.getDirection(mainStations, errors);

        assertThat(result).isEqualTo(List.of("stationA", "stationB"));
    }

    @Test
    void getDirection_withNullMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = null;
        List<String> errors = mock(List.class);

        List<String> result = utils.getDirection(mainStations, errors);

        assertThat(result).isEmpty();
        verify(errors).add("No main stations found");
    }

    @Test
    void getDirection_withBlankMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = "   ";
        List<String> errors = mock(List.class);

        List<String> result = utils.getDirection(mainStations, errors);

        assertThat(result).isEmpty();
        verify(errors).add("No main stations found");
    }

    @Test
    void getStopDuration_withValidStopDuration() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "15 min";
        List<String> errors = mock(List.class);

        Duration result = utils.getStopDuration(duration, errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void getStopDuration_withNecunoscută() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "necunoscută";
        List<String> errors = mock(List.class);

        Duration result = utils.getStopDuration(duration, errors);

        assertThat(result).isNull();
        verifyNoInteractions(errors);
    }

    @Test
    void getStopDuration_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "invalid-format";
        List<String> errors = mock(List.class);

        Duration result = utils.getStopDuration(duration, errors);

        assertThat(result).isNull();
        verify(errors).add("Could not convert label invalid-format into duration");
    }
}
