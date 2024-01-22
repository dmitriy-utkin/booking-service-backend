package ru.example.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.dao.RoleType;
import ru.example.booking.service.UserService;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UpdateUserRequest;
import ru.example.booking.dto.user.UserResponse;
import ru.example.booking.dto.user.UserResponseList;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseList findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findById(id, userDetails.getUsername());
    }

    @GetMapping("/username/{username}")
    public UserResponse findByUsername(@PathVariable("username") String username,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(username, userDetails.getUsername());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> save(@RequestParam RoleType role,
                                             @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(role, request));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return userService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteById(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
