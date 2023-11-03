package com.hcvision.hcvisionserver.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue()
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Enumerated()
    private Role role;
    private boolean activated;

    public User(String firstName, String lastName, String email, String password, Role appUserRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = appUserRole;
    }

}