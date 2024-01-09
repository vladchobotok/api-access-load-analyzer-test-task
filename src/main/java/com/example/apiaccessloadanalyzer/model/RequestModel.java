package com.example.apiaccessloadanalyzer.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Component
public class RequestModel {

    //annotations that are used here can be used for validation with annotations @Valid, @Validated
    @Pattern(regexp = "^(?!0\\d{0,2}\\.)(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)" +
            "(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)){3}$",
            message = "Invalid IP address")
    @NotEmpty(message = "IP address cannot be empty")
    private String ip;

    @NotNull(message = "Date cannot be empty")
    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDateTime dateTime;

    @Pattern(regexp = "^[A-Z]+$", message = "Invalid request method")
    @NotEmpty(message = "Request method cannot be empty")
    private String requestMethod;

    @Pattern(regexp = "^\\/(?:\\w+\\/?)*$",
            message = "Invalid URI")
    @NotEmpty(message = "URI cannot be empty")
    private String uri;

    @Min(value = 100, message = "Status code must be at least 100")
    @Max(value = 599, message = "Status code must be at most 599")
    @NotNull(message = "Request status cannot be empty")
    private int requestStatus;
}
