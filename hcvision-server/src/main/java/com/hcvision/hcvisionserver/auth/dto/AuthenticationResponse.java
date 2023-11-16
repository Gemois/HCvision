package com.hcvision.hcvisionserver.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.user.dto.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("confirmed")
    private boolean confirmed;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("issued_at")
    private LocalDateTime issuedAt;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

}
