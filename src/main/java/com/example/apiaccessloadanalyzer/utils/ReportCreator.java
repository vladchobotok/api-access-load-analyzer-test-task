package com.example.apiaccessloadanalyzer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportCreator {

    private final CounterManager counterManager;

    @Autowired
    public ReportCreator(CounterManager counterManager) {
        this.counterManager = counterManager;
    }

    public String createReport(Map<String, Long> topNUris, Map<String, Long> requestsPerSeconds){
        StringBuilder output = new StringBuilder();
        output.append("Results:\n");
        topNUris.forEach((key, value) -> {
            output.append(key)
                    .append(value)
                    .append(" time");
            if(value != 1){
                output.append("s");
            }
            output.append("\n");
        });

        output.append("\n---- Statistics ----\n");
        output.append("Requests per seconds:\n");
        requestsPerSeconds.forEach((key, value) -> {
            output.append(key)
                    .append(value)
                    .append(" request");
            if(value != 1){
                output.append("s");
            }
            output.append("\n");
        });
        output.append("------------------\n");

        output.append("\n---- Counters ----\n");
        output.append("Total rows - ").append(counterManager.getTotalRows()).append("\n");
        output.append("Valid rows - ").append(counterManager.getValidRows()).append("\n");
        output.append("Processed total time - ").append(counterManager.getProcessedTotalTime()).append(" sec\n");
        output.append("------------------\n");

        return output.toString();
    }
}
