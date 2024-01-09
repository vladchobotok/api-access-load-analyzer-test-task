package com.example.apiaccessloadanalyzer;

import com.example.apiaccessloadanalyzer.controller.LoadAnalyzerController;
import com.example.apiaccessloadanalyzer.service.FileService;
import com.example.apiaccessloadanalyzer.service.StatisticsService;
import com.example.apiaccessloadanalyzer.utils.CounterManager;
import com.example.apiaccessloadanalyzer.utils.ReportCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = LoadAnalyzerController.class)
class LoadAnalyzerControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private FileService fileService;

    @MockBean
    private ReportCreator reportCreator;

    @MockBean
    private CounterManager counterManager;

    @InjectMocks
    private LoadAnalyzerController loadAnalyzerController;

    @Test
    void uploadCsvFileAndGetAnalysisTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("csvFile",
                "requests.csv",
                "text/csv",
                "content".getBytes());

        when(fileService.convertFileToRequestModelList(any())).thenReturn(Collections.emptyList());
        when(statisticsService.getTopNURIs(anyList(), anyInt())).thenReturn(Collections.emptyMap());
        when(statisticsService.getRequestsPerSeconds(anyList())).thenReturn(Collections.emptyMap());
        when(reportCreator.createReport(anyMap(), anyMap())).thenReturn("test report");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/load_analysis")
                        .file(file)
                        .param("topN", "3")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"load_analysis_report.csv\""))
                .andExpect(content().string("test report"));

        verify(fileService).convertFileToRequestModelList(any());
        verify(statisticsService).getTopNURIs(anyList(), eq(3));
        verify(statisticsService).getRequestsPerSeconds(anyList());
        verify(reportCreator).createReport(anyMap(), anyMap());

        verify(counterManager).resetCounters();
        verify(counterManager).startTimer();
        verify(counterManager).stopTimer();
    }
}
