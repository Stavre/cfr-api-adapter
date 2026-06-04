package com.stavre.cfrapiadapter.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stavre.cfrapiadapter.dto.request.RequestTrainTimeTableDto;
import com.stavre.cfrapiadapter.proxy.TrainTimeTableProxy;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class TrainControllerTest {

    private static final String MAIN_BRANCH =
            "TrainBranchDto[name=Main branch, originStation=null, destinationStation=null]";

    private static final String STOPS = "$.stops['" + MAIN_BRANCH + "']";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainTimeTableProxy proxy;

    @Test
    void getTrainTimeTable_whenCfrReturnsTrainNotFound_returns400WithCfrError() throws Exception {
        String html = FileUtils.readFileToString(
                new File(Thread.currentThread().getContextClassLoader()
                        .getResource("scraper/train/train-not-found-token-page.html")
                        .getFile()),
                StandardCharsets.UTF_8);

        when(proxy.getTrainTokenPage(eq("001743"), anyString())).thenReturn(html);

        mockMvc.perform(get("/train/001743"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Nu a fost găsit niciun tren cu acest număr!"));
    }

    @Test
    void getTrainTimeTable_whenCfrReturnsValidTrain_returns200WithTrainData() throws Exception {
        String tokenPageHtml = FileUtils.readFileToString(
                new File(Thread.currentThread().getContextClassLoader()
                        .getResource("scraper/train/train-10101-token-page.html")
                        .getFile()),
                StandardCharsets.UTF_8);

        String timetableHtml = FileUtils.readFileToString(
                new File(Thread.currentThread().getContextClassLoader()
                        .getResource("scraper/train/train-10101-timetable-page.html")
                        .getFile()),
                StandardCharsets.UTF_8);

        when(proxy.getTrainTokenPage(eq("10101"), anyString())).thenReturn(tokenPageHtml);
        when(proxy.getTrainTimeTable(any(RequestTrainTimeTableDto.class))).thenReturn(timetableHtml);

        mockMvc.perform(get("/train/10101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.number").value("10101"));
    }

    @Test
    void getTrainTimeTable_whenCfrReturnsValidTrain_returnsCorrectStopDetails() throws Exception {
        String tokenPageHtml = FileUtils.readFileToString(
                new File(Thread.currentThread().getContextClassLoader()
                        .getResource("scraper/train/train-10101-token-page.html")
                        .getFile()),
                StandardCharsets.UTF_8);
        String timetableHtml = FileUtils.readFileToString(
                new File(Thread.currentThread().getContextClassLoader()
                        .getResource("scraper/train/train-10101-timetable-page.html")
                        .getFile()),
                StandardCharsets.UTF_8);

        when(proxy.getTrainTokenPage(eq("10101"), anyString())).thenReturn(tokenPageHtml);
        when(proxy.getTrainTimeTable(any(RequestTrainTimeTableDto.class))).thenReturn(timetableHtml);

        mockMvc.perform(get("/train/10101").param("date", "04.06.2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STOPS + ".length()").value(3))

                // stop 0 – București Nord (origin: departure only, no arrival)
                .andExpect(jsonPath(STOPS + "[0].station").value("București Nord"))
                .andExpect(jsonPath(STOPS + "[0].journeyKm").value(0))
                .andExpect(jsonPath(STOPS + "[0].arrival").value(nullValue()))
                .andExpect(jsonPath(STOPS + "[0].departure").value("2026-06-04T01:10:00"))

                // stop 1 – Parc Mogoșoaia (intermediate: arrival, departure, 1-min stop)
                .andExpect(jsonPath(STOPS + "[1].station").value("Parc Mogoșoaia"))
                .andExpect(jsonPath(STOPS + "[1].journeyKm").value(11))
                .andExpect(jsonPath(STOPS + "[1].arrival").value("2026-06-04T01:20:00"))
                .andExpect(jsonPath(STOPS + "[1].departure").value("2026-06-04T01:21:00"))
                .andExpect(jsonPath(STOPS + "[1].stopDuration").value("PT1M"))

                // stop 2 – Aeroport Henri Coandă (destination: arrival only, no departure)
                .andExpect(jsonPath(STOPS + "[2].station").value("Aeroport Henri Coandă"))
                .andExpect(jsonPath(STOPS + "[2].journeyKm").value(19))
                .andExpect(jsonPath(STOPS + "[2].arrival").value("2026-06-04T01:31:00"))
                .andExpect(jsonPath(STOPS + "[2].departure").value(nullValue()));
    }
}
