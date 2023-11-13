package com.hcvision.hcvisionserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {

    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPassword;
}
