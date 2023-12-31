package com.hcvision.hcvisionserver.auth.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationTokenResponse {

    @JsonProperty("confirmed")
    private boolean confirmed;

    @JsonProperty("confirmed_at")
    private LocalDateTime confirmedAt;

    @JsonProperty("msg")
    private String msg;

}
