package ru.example.booking.controller.defaults;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UserResponse;
import ru.example.booking.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;

    @PostMapping("/account")
    public ResponseEntity<UserResponse> createAccount(@RequestParam("role") RoleType role, @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(role, request));
    }

}
