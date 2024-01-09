package com.example.apiaccessloadanalyzer.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import static java.lang.System.currentTimeMillis;

@Component
@Getter
public class CounterManager {

    private long totalRows;
    private long validRows;
    private double processedTotalTime;
    private long startTime;
    private long endTime;

    public void incrementTotalRows() {
        totalRows++;
    }

    public void incrementValidRows() {
        validRows++;
    }

    public void startTimer(){
        if(startTime == 0) {
            startTime = currentTimeMillis();
        }
    }

    public void stopTimer(){
        if(startTime != 0){
            endTime = currentTimeMillis();
            countProcessedTime();
        }
    }
    private void countProcessedTime() {
        processedTotalTime += (double) (endTime - startTime) / 1000;
    }
    public void resetCounters(){
        totalRows = 0;
        validRows = 0;
        processedTotalTime = 0.0;
        startTime = 0;
        endTime = 0;
    }

    public long getNextIndexOfTotalRows(){
        return totalRows + 1;
    }
}
