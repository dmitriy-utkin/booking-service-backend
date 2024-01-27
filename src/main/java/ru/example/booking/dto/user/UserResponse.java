package ru.example.booking.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.postrgres.RoleType;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private Set<RoleType> roles;

}
