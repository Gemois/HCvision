package com.hcvision.hcvisionserver.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @JsonProperty("confirmation_token")
    private String token;

    @JsonProperty("password")
    private String password;
}
