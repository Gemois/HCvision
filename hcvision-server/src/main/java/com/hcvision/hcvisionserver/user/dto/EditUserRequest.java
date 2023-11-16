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
public class EditUserRequest {

    @JsonProperty("firstname")
    private String newFirstName;

    @JsonProperty("lastname")
    private String newLastName;

    @JsonProperty("email")
    private String newEmail;

    @JsonProperty("password")
    private String newPassword;

}
