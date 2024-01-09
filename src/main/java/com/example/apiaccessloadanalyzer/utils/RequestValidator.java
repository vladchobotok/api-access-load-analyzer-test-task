package com.example.apiaccessloadanalyzer.utils;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Validated
@Component
public class RequestValidator
{
    // Another way to validate data to realize this method
//    public void validateRequestModel(@Valid RequestModel requestModel){
//
//    }

    public static final String STARTING_ERROR_MSG_TEMPLATE = "Error while parsing file, line ";
    private final CounterManager counterManager;

    @Autowired
    public RequestValidator(CounterManager counterManager) {
        this.counterManager = counterManager;
    }

    public boolean validateRequestModel(RequestModel requestModel){
        return validateIpAddress(requestModel.getIp())
                && validateDateTime(requestModel.getDateTime())
                && validateRequestMethod(requestModel.getRequestMethod())
                && validateUri(requestModel.getUri())
                && validateRequestStatus(requestModel.getRequestStatus());
    }
    private boolean validateIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": IP address cannot be empty");
            return false;
        }

        if (!ipAddress.matches("^(?!0\\d{0,2}\\.)(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)"
                + "(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)){3}$")) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": invalid IP address");
            return false;
        }
        return true;
    }

    private boolean validateDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": date cannot be empty");
            return false;
        }

        if (dateTime.isAfter(LocalDateTime.now())) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": date must be in the past or present");
            return false;
        }
        return true;
    }

    private boolean validateRequestMethod(String requestMethod) {
        if (requestMethod == null || requestMethod.isEmpty()) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": request method cannot be empty");
            return false;
        }

        List<String> allHttpMethods = Arrays.stream(HttpMethod.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        if (!requestMethod.matches("^[A-Z]+$") || !allHttpMethods.contains(requestMethod)) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": invalid request method");
            return false;
        }
        return true;
    }

    private boolean validateUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": URI cannot be empty");
            return false;
        }

        if (!uri.matches("^\\/(?:\\w+\\/?)*$")) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    +  ": invalid URI address");
            return false;
        }
        return true;
    }

    private boolean validateRequestStatus(int requestStatus) {
        if (requestStatus < 100 || requestStatus > 599) {
            log.error(STARTING_ERROR_MSG_TEMPLATE
                    + counterManager.getNextIndexOfTotalRows()
                    + ": status code must be between 100 and 599");
            return false;
        }

        return true;
    }
}
