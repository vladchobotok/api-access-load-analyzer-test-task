package com.example.apiaccessloadanalyzer;

import com.example.apiaccessloadanalyzer.utils.CounterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterManagerTests {

    @Mock
    private CounterManager mockCounterManager;

    @Spy
    private CounterManager spyCounterManager = new CounterManager();

    @BeforeEach
    void beforeEach() {
        spyCounterManager.resetCounters();
    }

    @Test
    void incrementTotalRowsTest_shouldInvoke() {
        mockCounterManager.incrementTotalRows();
        mockCounterManager.incrementTotalRows();
        mockCounterManager.incrementTotalRows();

        verify(mockCounterManager, Mockito.times(3)).incrementTotalRows();
    }

    @Test
    void incrementValidRowsTest_shouldInvoke() {
        mockCounterManager.incrementValidRows();
        mockCounterManager.incrementValidRows();
        mockCounterManager.incrementValidRows();

        verify(mockCounterManager, Mockito.times(3)).incrementValidRows();
    }

    @Test
    void startAndStopTimer_shouldInvoke() {
        mockCounterManager.startTimer();
        mockCounterManager.stopTimer();

        verify(mockCounterManager, times(1)).startTimer();
        verify(mockCounterManager, times(1)).stopTimer();
    }

    @Test
    void timerShouldReturnTheExactTime() throws InterruptedException {
        spyCounterManager.startTimer();
        Thread.sleep(2000);
        spyCounterManager.stopTimer();

        assertEquals(2.0, spyCounterManager.getProcessedTotalTime(), 0.1);
    }

    @Test
    void resetCounters_shouldResetCounters() throws InterruptedException {
        spyCounterManager.incrementTotalRows();
        spyCounterManager.incrementValidRows();
        spyCounterManager.startTimer();
        Thread.sleep(1000);
        spyCounterManager.stopTimer();

        assertNotEquals(0L, spyCounterManager.getTotalRows());
        assertNotEquals(0L, spyCounterManager.getValidRows());
        assertNotEquals(0.0, spyCounterManager.getProcessedTotalTime());

        spyCounterManager.resetCounters();

        verify(spyCounterManager, times(2)).resetCounters();
        assertEquals(0L, spyCounterManager.getTotalRows());
        assertEquals(0L, spyCounterManager.getValidRows());
        assertEquals(0.0, spyCounterManager.getProcessedTotalTime());
    }

    @Test
    void getNextIndexOfTotalRows_shouldReturnNextIndexOfTotalRows() {
        spyCounterManager.incrementTotalRows();
        spyCounterManager.incrementTotalRows();
        spyCounterManager.incrementTotalRows();

        assertEquals(4L, spyCounterManager.getNextIndexOfTotalRows());
    }
}
