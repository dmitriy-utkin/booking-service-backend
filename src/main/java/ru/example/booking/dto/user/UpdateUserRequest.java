package ru.example.booking.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.postrgres.RoleType;

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
