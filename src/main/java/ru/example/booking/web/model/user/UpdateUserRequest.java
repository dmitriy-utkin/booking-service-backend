package ru.example.booking.web.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.model.RoleType;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String username;

    private String password;

    private String email;

    private Set<RoleType> roles;
}
