package com.hcvision.hcvisionserver.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("confirmed")
    private boolean confirmed;

    @JsonProperty("registeredAt")
    private LocalDateTime registeredAt;

    @JsonProperty("msg")
    private String msg;

}
