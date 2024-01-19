package ru.example.booking.web.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 20, message = "Username length should be between 5 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 20, message = "Password length should be between 4 and 20 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

}
