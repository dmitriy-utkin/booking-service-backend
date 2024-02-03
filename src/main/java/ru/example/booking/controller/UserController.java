package ru.example.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import ru.example.booking.dto.defaults.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UpdateUserRequest;
import ru.example.booking.dto.user.UserResponse;
import ru.example.booking.dto.user.UserResponseList;
import ru.example.booking.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Find all users",
            description = "To find all users who was registered in the service",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"user", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = UserResponseList.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseList findAll() {
        return userService.findAll();
    }

    @Operation(
            summary = "Find user by ID",
            description = "To find user by ID (user with role 'USER' can find information about himself)",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"user", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findById(id, userDetails.getUsername());
    }

    @Operation(
            summary = "Find user by username",
            description = "To find user by username (user with role 'USER' can find information about himself)",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"user", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping("/username/{username}")
    public UserResponse findByUsername(@PathVariable("username") String username,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(username, userDetails.getUsername());
    }

    @Operation(
            summary = "Create new user",
            description = "To create a user after authorization",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"user", "POST"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201"

            ),
            @ApiResponse(
                    responseCode = "208",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestParam RoleType role,
                                             @RequestBody CreateUserRequest request) {
        userService.save(role, request);
    }

    @Operation(
            summary = "Update existed user",
            description = "To update information about existed user (user with role 'USER' can update information about himself)",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"user", "PUT"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }

            ),
            @ApiResponse(
                    responseCode = "208",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request,
                               @AuthenticationPrincipal UserDetails userDetails) {
        return userService.update(id, request, userDetails.getUsername());
    }

    @Operation(
            summary = "Delete user by id",
            description = "To delete existed user by ID (user with role 'USER' can delete only their account)",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"user", "DELETE"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteById(id, userDetails.getUsername());
    }
}
