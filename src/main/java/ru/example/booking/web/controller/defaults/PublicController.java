package ru.example.booking.web.controller.defaults;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.model.RoleType;
import ru.example.booking.service.UserService;
import ru.example.booking.web.model.user.CreateUserRequest;
import ru.example.booking.web.model.user.UserResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping("/account")
    public ResponseEntity<UserResponse> createAccount(@RequestParam("role") RoleType role, @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userMapper.userToUserResponse(
                        userService.save(role, userMapper.createRequestToUser(request))
                )
        );
    }

}
