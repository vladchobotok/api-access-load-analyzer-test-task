package com.example.apiaccessloadanalyzer.controller;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import com.example.apiaccessloadanalyzer.service.FileService;
import com.example.apiaccessloadanalyzer.service.StatisticsService;
import com.example.apiaccessloadanalyzer.utils.CounterManager;
import com.example.apiaccessloadanalyzer.utils.ReportCreator;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoadAnalyzerController {

    private final StatisticsService statisticsService;
    private final FileService fileService;
    private final ReportCreator reportCreator;

    private final CounterManager counterManager;

    @Autowired
    public LoadAnalyzerController(StatisticsService statisticsService,
                                  FileService fileService,
                                  ReportCreator reportCreator,
                                  CounterManager counterManager) {
        this.statisticsService = statisticsService;
        this.fileService = fileService;
        this.reportCreator = reportCreator;
        this.counterManager = counterManager;
    }

    @PostMapping(value = "/load_analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "text/csv")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Report created successfully"),
            @ApiResponse(code = 400, message = "Input is invalid")
    })

    public String uploadCsvFileAndGetAnalysis(@RequestPart("csvFile") MultipartFile csvFile,
                                              @RequestParam("topN") @Min(0) Integer topN,
                                              HttpServletResponse response) throws IOException {
        counterManager.resetCounters();
        counterManager.startTimer();

        List<RequestModel> requestModelList = fileService.convertFileToRequestModelList(csvFile);
        Map<String, Long> topNUris = statisticsService.getTopNURIs(requestModelList, topN);
        Map<String, Long> requestsPerSeconds = statisticsService.getRequestsPerSeconds(requestModelList);

        counterManager.stopTimer();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"load_analysis_report.csv\"");
        return reportCreator.createReport(topNUris, requestsPerSeconds);
    }
}