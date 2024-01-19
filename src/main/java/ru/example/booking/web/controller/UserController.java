package ru.example.booking.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.model.RoleType;
import ru.example.booking.service.UserService;
import ru.example.booking.web.model.user.CreateUserRequest;
import ru.example.booking.web.model.user.UpdateUserRequest;
import ru.example.booking.web.model.user.UserResponse;
import ru.example.booking.web.model.user.UserResponseList;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<UserResponseList> findAll() {
        return ResponseEntity.ok(userMapper.userListToResponseList(userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userMapper.userToUserResponse(userService.findById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userMapper.userToUserResponse(userService.findByUsername(username)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestParam RoleType role,
                                             @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userMapper.userToUserResponse(
                        userService.save(role, userMapper.createRequestToUser(request))
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(
                userMapper.userToUserResponse(
                        userService.update(id, userMapper.updateRequestToUser(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
