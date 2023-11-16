package com.hcvision.hcvisionserver.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.user.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue()
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Enumerated()
    private Role role;
    private boolean activated;

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public interface ProjectUser {

        @JsonProperty("firstname")
        String getFirstName();

        @JsonProperty("lastname")
        String getLastName();

        @JsonProperty("email")
        String getEmail();

        @JsonProperty("role")
        String getRole();
    }

}