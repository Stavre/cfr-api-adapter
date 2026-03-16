package com.stavre.cfrapiadapter.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

@ExtendWith(MockitoExtension.class)
class AdapterUtilsTest {

    public static final String DATE = "10.01.2024";
    public static final String TIME = "14:30";
    public static final String DATE1 = "10.02.2024";
    public static final String COULD_NOT_COMPUTE_TIMESTAMP = "Could not compute timestamp";
    @Mock
    private DateTimeUtils dateTimeUtils;

    @InjectMocks
    private AdapterUtils utils;

    private static final String PLATFORM_STRING_IS_NULL_OR_BLANK = "Platform string is null or blank.";

    @Test
    void shouldReturnNull_WhenInputIsNull() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform(null, errors);

        assertThat(result).isNull();
    }

    @Test
    void shouldAddError_WhenInputIsNull() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform(null, errors);

        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void shouldReturnNull_WhenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("  ", errors);

        assertThat(result).isNull();

    }

    @Test
    void shouldAddError_WhenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform("   ", errors);

        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void shouldReturnNull_WhenFormatDoesNotMatch() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("Linia 4A", errors); // wrong case

        assertThat(result).isNull();
    }

    @Test
    void shouldAddError_WhenFormatDoesNotMatch() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform("Linia 4A", errors); // wrong case

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
    }

    @Test
    void shouldNotAddError_WhenFormatIsValid() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform("linia 4A", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractPlatform_WhenExtraSpacesArePresent() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("  linia    X123   ", errors);

        assertThat(result).isEqualTo("X123");
    }

    @Test
    void shouldNotAddError_WhenExtraSpacesArePresent() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform("  linia    X123   ", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNull_WhenNothingAfterLinia() {
        List<String> errors = new ArrayList<>();

        String result = utils.getTrainPlatform("linia ", errors);

        assertThat(result).isNull();
    }

    @Test
    void shouldAddError_WhenNothingAfterLinia() {
        List<String> errors = new ArrayList<>();

        utils.getTrainPlatform("linia ", errors);

        assertThat(errors)
                .hasSize(1)
                .first()
                .isEqualTo("Platform string does not match expected format: linia ");
    }

    @Test
    void shouldReturnTimestamp_whenDateAndTimeAreValid() {
        List<String> errors = new ArrayList<>();

        LocalDate date = LocalDate.of(2024, 2, 10);
        LocalTime time = LocalTime.of(14, 30);

        when(dateTimeUtils.convertDate(DATE1)).thenReturn(Optional.of(date));
        when(dateTimeUtils.convertTime(TIME)).thenReturn(Optional.of(time));

        LocalDateTime result = utils.getTimestamp(DATE1, TIME, errors);

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 2, 10, 14, 30));
    }

    @Test
    void shouldNotAddError_whenDateAndTimeAreValid() {
        List<String> errors = new ArrayList<>();

        LocalDate date = LocalDate.of(2024, 2, 10);
        LocalTime time = LocalTime.of(14, 30);

        when(dateTimeUtils.convertDate(DATE1)).thenReturn(Optional.of(date));
        when(dateTimeUtils.convertTime(TIME)).thenReturn(Optional.of(time));

        utils.getTimestamp(DATE1, TIME, errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNull_whenInputIsNull() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay(null, errors);

        assertThat(result).isNull();
    }

    @Test
    void shouldAddError_whenInputIsNull() {
        List<String> errors = new ArrayList<>();

        utils.getDelay(null, errors);

        assertThat(errors)
                .containsExactly("Delay string is null or blank");
    }

    @Test
    void shouldReturnNull_whenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("  ", errors);

        assertThat(result).isNull();

    }

    @Test
    void shouldAddError_whenInputIsBlank() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("  ", errors);

        assertThat(errors)
                .containsExactly("Delay string is null or blank");
    }

    @Test
    void shouldReturnZeroDuration_whenInputIsLaTimp() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("la timp", errors);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldNotAddError_whenInputIsLaTimp() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("la timp", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnZeroDuration_whenInputIsLaTimpWithAsterisk() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("la timp*", errors);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldNotAddError_whenInputIsLaTimpWithAsterisk() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("la timp*", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractDelay_whenValidDelayString() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+7 min (întârziere)", errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(7));
    }

    @Test
    void shouldNotAddError_whenValidDelayString() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("+7 min (întârziere)", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldExtractDelay_whenValidDelayStringWithAsterisk() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+3 min (întârziere)*", errors);

        assertThat(result).isEqualTo(Duration.ofMinutes(3));
    }

    @Test
    void shouldNotAddError_whenValidDelayStringWithAsterisk() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("+3 min (întârziere)*", errors);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnNull_whenMissingSpaceBeforeMin() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("+5min", errors);

        assertThat(result).isNull();

    }

    @Test
    void shouldAddError_whenMissingSpaceBeforeMin() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("+5min", errors);

        assertThat(errors)
                .containsExactly("Could not extract delay from string: +5min");
    }

    @Test
    void shouldReturnNull_whenInvalidFormat() {
        List<String> errors = new ArrayList<>();

        Duration result = utils.getDelay("random text", errors);

        assertThat(result).isNull();

    }

    @Test
    void shouldAddError_whenInvalidFormat() {
        List<String> errors = new ArrayList<>();

        utils.getDelay("random text", errors);

        assertThat(errors)
                .containsExactly("Could not extract delay from string: random text");
    }

    @Test
    void getTrainPlatform_withValidComplexPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia 2A";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isEqualTo("2A");
    }

    @Test
    void getTrainPlatformDoesNotAddError_withValidComplexPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia 2A";
        List<String> errors = mock(List.class);

        utils.getTrainPlatform(platform, errors);

        verifyNoInteractions(errors);
    }

    @Test
    void getTrainPlatformReturnNull_withNullPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = null;
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
    }

    @Test
    void getTrainPlatformAddError_withNullPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = null;
        List<String> errors = mock(List.class);

        utils.getTrainPlatform(platform, errors);

        verify(errors).add(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void getTrainPlatformReturnNull_withBlankPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = " ";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
    }

    @Test
    void getTrainPlatformAddError_withBlankPlatformString() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = " ";
        List<String> errors = mock(List.class);

        utils.getTrainPlatform(platform, errors);

        verify(errors).add(PLATFORM_STRING_IS_NULL_OR_BLANK);
    }

    @Test
    void getTrainPlatformReturnNull_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia2";
        List<String> errors = mock(List.class);

        String result = utils.getTrainPlatform(platform, errors);

        assertThat(result).isNull();
    }

    @Test
    void getTrainPlatformAddError_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String platform = "linia2";
        List<String> errors = mock(List.class);

        utils.getTrainPlatform(platform, errors);

        verify(errors).add("Platform string does not match expected format: linia2");
    }

    @Test
    void getTimestamp_withValidDateAndTime() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String time = "19:30";

        LocalDateTime result = utils.getTimestamp(DATE, time, mock(List.class));

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 1, 10, 19, 30));
    }

    @Test
    void getTimestampReturnNull_withInvalidDate() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "invalid-date";
        String time = "19:30";

        List<String> errors = mock(List.class);
        LocalDateTime result = utils.getTimestamp(date, time, errors);

        assertThat(result).isNull();
    }

    @Test
    void getTimestampAddError_withInvalidDate() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "invalid-date";
        String time = "19:30";

        List<String> errors = new ArrayList<>();
        utils.getTimestamp(date, time, errors);

        assertThat(errors).containsAll(List.of(
                "Could not convert date invalid-date to date object",
                COULD_NOT_COMPUTE_TIMESTAMP
        ));
    }

    @Test
    void getTimestampReturnNull_withInvalidTime() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "10.01.2024";
        String time = "invalid-time";

        List<String> errors = mock(List.class);
        LocalDateTime result = utils.getTimestamp(date, time, errors);

        assertThat(result).isNull();
    }

    @Test
    void getTimestampAddErrors_withInvalidTime() {
        AdapterUtils utils = new AdapterUtils(new DateTimeUtils());

        String date = "10.01.2024";
        String time = "invalid-time";

        List<String> errors = new ArrayList<>();
        utils.getTimestamp(date, time, errors);

        assertThat(errors).containsAll(List.of(
                "Could not convert time invalid-time to time object",
                COULD_NOT_COMPUTE_TIMESTAMP
        ));
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
    void getDelayReturnNull_withEmptyInput() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isNull();
    }

    @Test
    void getDelayAddError_withEmptyInput() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "";
        List<String> errors = mock(List.class);

        utils.getDelay(input, errors);

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
    void getDelayReturnNull_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "invalid format";
        List<String> errors = mock(List.class);

        Duration result = utils.getDelay(input, errors);

        assertThat(result).isNull();
    }

    @Test
    void getDelayAddError_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String input = "invalid-format";
        List<String> errors = mock(List.class);

        utils.getDelay(input, errors);

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
    void getDirectionReturnEmpty_withNullMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = null;
        List<String> errors = mock(List.class);

        List<String> result = utils.getDirection(mainStations, errors);

        assertThat(result).isEmpty();
    }

    @Test
    void getDirectionAddError_withNullMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = null;
        List<String> errors = mock(List.class);

        utils.getDirection(mainStations, errors);

        verify(errors).add("No main stations found");
    }

    @Test
    void getDirectionReturnEmpty_withBlankMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = "   ";
        List<String> errors = mock(List.class);

        List<String> result = utils.getDirection(mainStations, errors);

        assertThat(result).isEmpty();
    }

    @Test
    void getDirectionAddError_withBlankMainStations() {
        AdapterUtils utils = new AdapterUtils(null);

        String mainStations = "  ";
        List<String> errors = mock(List.class);

        utils.getDirection(mainStations, errors);

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
    void getStopDurationReturnNull_withNecunoscută() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "necunoscută";
        List<String> errors = mock(List.class);

        Duration result = utils.getStopDuration(duration, errors);

        assertThat(result).isNull();
    }

    @Test
    void getStopDurationDoesNotAddError_withNecunoscută() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "necunoscută";
        List<String> errors = mock(List.class);

        utils.getStopDuration(duration, errors);

        verifyNoInteractions(errors);
    }

    @Test
    void getStopDurationReturnNull_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "invalid format";
        List<String> errors = mock(List.class);

        Duration result = utils.getStopDuration(duration, errors);

        assertThat(result).isNull();
    }

    @Test
    void getStopDurationAddsError_withInvalidFormat() {
        AdapterUtils utils = new AdapterUtils(null);

        String duration = "invalid-format";
        List<String> errors = mock(List.class);

        utils.getStopDuration(duration, errors);

        verify(errors).add("Could not convert label invalid-format into duration");
    }
}
