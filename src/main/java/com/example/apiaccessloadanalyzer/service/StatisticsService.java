package com.example.apiaccessloadanalyzer.service;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import com.example.apiaccessloadanalyzer.utils.CounterManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class StatisticsService {

    public static final String STATISTICS_DELIMITER = " - ";
    private final CounterManager counterManager;

    @Autowired
    public StatisticsService(CounterManager counterManager) {
        this.counterManager = counterManager;
    }

    private Map<String, Long> calculateURIFrequency(List<RequestModel> requestModels) {
        return requestModels.stream()
                .collect(Collectors.groupingBy(
                        requestModel -> requestModel.getUri()
                                + STATISTICS_DELIMITER
                                + requestModel.getRequestMethod()
                                + STATISTICS_DELIMITER,
                        Collectors.counting()));
    }

    public Map<String, Long> getTopNURIs(List<RequestModel> requestModels, int n) {
        try {
            Map<String, Long> uriFrequencyMap = calculateURIFrequency(requestModels);

            return uriFrequencyMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(n)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (o1, o2) -> o1,
                            LinkedHashMap::new));
        }
        catch (IllegalArgumentException e){
            log.error("Invalid topN value");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid topN value", e);
        }
    }

    private Map<String, Long> calculateDateTimeFrequency(List<RequestModel> requestModels) {
        return requestModels.stream()
                .collect(Collectors.groupingBy(
                        requestModel -> requestModel.getDateTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss"))
                                + STATISTICS_DELIMITER,
                        Collectors.counting()));
    }

    public Map<String, Long> getRequestsPerSeconds(List<RequestModel> requestModels) {
        Map<String, Long> dateTimeFrequencyMap = calculateDateTimeFrequency(requestModels);

        return dateTimeFrequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByKey().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (o1, o2) -> o1,
                        LinkedHashMap::new));
    }
}
