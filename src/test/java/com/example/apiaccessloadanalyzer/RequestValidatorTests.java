package com.example.apiaccessloadanalyzer;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import com.example.apiaccessloadanalyzer.utils.CounterManager;
import com.example.apiaccessloadanalyzer.utils.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class RequestValidatorTests {

    @InjectMocks
    private RequestValidator requestValidator;

    @Mock
    private CounterManager counterManager;

    RequestModel[] requestModels = new RequestModel[21];

    @BeforeEach
    void beforeEach(){
        requestModels[0] = new RequestModel("192.4.23.3",
                LocalDateTime.parse("28/07/2006:10:22:04-0300",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")),
                "POST",
                "/account/info/",
                200);
        requestModels[1] = requestModels[0].toBuilder().ip("").build();
        requestModels[2] = requestModels[0].toBuilder().ip(null).build();
        requestModels[3] = requestModels[0].toBuilder().ip("192.F.23.3").build();
        requestModels[4] = requestModels[0].toBuilder().ip("0.4.23.3").build();
        requestModels[5] = requestModels[0].toBuilder().ip("192.4.256.3").build();
        requestModels[6] = requestModels[0].toBuilder().dateTime(null).build();
        requestModels[7] = requestModels[0].toBuilder().dateTime(LocalDateTime.parse("28/07/2106:10:22:04-0300",
                DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ"))).build();
        requestModels[8] = requestModels[0].toBuilder().requestMethod(null).build();
        requestModels[9] = requestModels[0].toBuilder().requestMethod("").build();
        requestModels[10] = requestModels[0].toBuilder().requestMethod("POSTER").build();
        requestModels[11] = requestModels[0].toBuilder().uri(null).build();
        requestModels[12] = requestModels[0].toBuilder().uri("").build();
        requestModels[13] = requestModels[0].toBuilder().uri("/abc\\").build();
        requestModels[14] = requestModels[0].toBuilder().uri("abc/").build();
        requestModels[15] = requestModels[0].toBuilder().requestStatus(2000).build();
        requestModels[16] = requestModels[0].toBuilder().requestStatus(-1).build();
    }

    @Test
    void validateRequestModel_shouldProperlyValidate() {
        boolean result = requestValidator.validateRequestModel(requestModels[0]);
        assertTrue(result);
    }

    @Test
    void validateIpAddress_shouldReturnThatIPAddressCannotBeEmptyWhenIpIsEmpty(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[1]);
        assertThat(output.getOut()).contains("IP address cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateIpAddress_shouldReturnThatIPAddressCannotBeEmptyWhenIpIsNull(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[2]);
        assertThat(output.getOut()).contains("IP address cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateIpAddress_shouldReturnThatIPAddressIsInvalidWhenIpContainsLetters(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[3]);
        assertThat(output.getOut()).contains("invalid IP address");
        assertFalse(result);
    }

    @Test
    void validateIpAddress_shouldReturnThatIPAddressIsInvalidWhenIpStartsWithZero(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[4]);
        assertThat(output.getOut()).contains("invalid IP address");
        assertFalse(result);
    }

    @Test
    void validateIpAddress_shouldReturnThatIPAddressIsInvalidWhenIpContainsNumbersMoreThan255(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[5]);
        assertThat(output.getOut()).contains("invalid IP address");
        assertFalse(result);
    }

    @Test
    void validateDateTime_shouldReturnThatDateCannotBeEmptyWhenDateIsNull(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[6]);
        assertThat(output.getOut()).contains("date cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateDateTime_shouldReturnThatDateMustBeInPresentOrPastWhenDateIsInFuture(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[7]);
        assertThat(output.getOut()).contains("date must be in the past or present");
        assertFalse(result);
    }

    @Test
    void validateRequestMethod_shouldReturnThatRequestMethodCannotBeEmptyWhenItIsNull(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[8]);
        assertThat(output.getOut()).contains("request method cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateRequestMethod_shouldReturnThatRequestMethodCannotBeEmptyWhenItIsEmpty(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[9]);
        assertThat(output.getOut()).contains("request method cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateRequestMethod_shouldReturnThatRequestMethodIsInvalidWhenItDoesNotExist(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[10]);
        assertThat(output.getOut()).contains("invalid request method");
        assertFalse(result);
    }

    @Test
    void validateUri_shouldReturnThatUriCannotBeEmptyWhenItIsNull(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[11]);
        assertThat(output.getOut()).contains("URI cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateUri_shouldReturnThatUriCannotBeEmptyWhenItIsEmpty(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[12]);
        assertThat(output.getOut()).contains("URI cannot be empty");
        assertFalse(result);
    }

    @Test
    void validateUri_shouldReturnThatUriIsInvalidWhenUsedWrongSlash(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[13]);
        assertThat(output.getOut()).contains("invalid URI address");
        assertFalse(result);
    }

    @Test
    void validateUri_shouldReturnThatUriIsInvalidWhenUriDoesNotContainSlash(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[14]);
        assertThat(output.getOut()).contains("invalid URI address");
        assertFalse(result);
    }

    @Test
    void validateRequestStatus_shouldReturnThatRequestStatusIsNotInRangeWhenItIsBigger(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[15]);
        assertThat(output.getOut()).contains("status code must be between 100 and 599");
        assertFalse(result);
    }

    @Test
    void validateRequestStatus_shouldReturnThatRequestStatusIsNotInRangeWhenItIsNegative(CapturedOutput output) {
        boolean result = requestValidator.validateRequestModel(requestModels[16]);
        assertThat(output.getOut()).contains("status code must be between 100 and 599");
        assertFalse(result);
    }
}