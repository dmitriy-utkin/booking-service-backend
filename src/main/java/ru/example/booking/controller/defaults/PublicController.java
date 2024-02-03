package ru.example.booking.controller.defaults;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;

    @Operation(
            summary = "Create new user",
            description = "To create a user without authorization",
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
            )
    })
    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@RequestParam("role") RoleType role, @RequestBody CreateUserRequest request) {
        userService.save(role, request);
    }

}
