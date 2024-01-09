package com.example.apiaccessloadanalyzer;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import com.example.apiaccessloadanalyzer.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {
    @InjectMocks
    private StatisticsService statisticsService;
    List<RequestModel> requestModelList;
    List<RequestModel> requestModelIncompleteList;

    @BeforeEach
    void beforeEach(){
        RequestModel requestModel1 = new RequestModel("192.4.23.3",
                LocalDateTime.parse("28/07/2006:10:22:04-0300",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")),
                "POST",
                "/account/info/",
                200);
        RequestModel requestModel2 = new RequestModel("192.4.23.4",
                LocalDateTime.parse("28/07/2006:10:22:04-0300",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")),
                "GET",
                "/account/info/",
                200);
        RequestModel requestModel3 = new RequestModel("192.4.23.5",
                LocalDateTime.parse("28/07/2006:10:23:04-0300",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")),
                "POST",
                "/account/info/",
                200);
        RequestModel requestModel4 = new RequestModel("192.4.23.6",
                LocalDateTime.parse("28/07/2006:10:24:04-0300",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")),
                "GET",
                "/account/info/",
                200);
        requestModelList = Arrays.asList(requestModel1, requestModel2, requestModel3, requestModel4);
        requestModelIncompleteList = Arrays.asList(requestModel1, requestModel4);

    }

    @Test
    void getTopNURIs_shouldReturnLessTopNURIsThanNumberOfRequests() {
        Map<String, Long> result = statisticsService.getTopNURIs(requestModelList, 3);

        assertEquals(2, result.size());
    }

    @Test
    void getTopNURIs_shouldReturnTheExactNumberOfRequests() {
        Map<String, Long> result = statisticsService.getTopNURIs(requestModelIncompleteList, 2);

        assertEquals(2, result.size());
    }

    @Test
    void getRequestsPerSeconds_shouldReturnLessValuesThanNumberOfRequests() {
        Map<String, Long> result = statisticsService.getRequestsPerSeconds(requestModelList);

        assertEquals(3, result.size());
    }

    @Test
    void getRequestsPerSeconds_shouldReturnTheExactNumberOfRequests() {
        Map<String, Long> result = statisticsService.getRequestsPerSeconds(requestModelIncompleteList);

        assertEquals(2, result.size());
    }
}