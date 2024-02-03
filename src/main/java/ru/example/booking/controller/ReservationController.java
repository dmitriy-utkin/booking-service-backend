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
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.ReservationResponseList;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.service.ReservationService;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "Find all reservations",
            description = "To find all reservation in the booking service",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"reservation", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = ReservationResponseList.class),
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
    public ReservationResponseList findAll() {
        return reservationService.findAll();
    }

    @Operation(
            summary = "Find reservation",
            description = "To find reservation in the booking service by reservation ID " +
                    "(user with role 'USER' can find only their reservations",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"reservation", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = ReservationResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return reservationService.findById(id, userDetails.getUsername());
    }

    @Operation(
            summary = "Create new reservation",
            description = "To create reservation in the booking service for available dates",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"reservation", "POST"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201"
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
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
            ),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReservation(@RequestBody UpsertReservationRequest request,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.booking(request, userDetails.getUsername());
    }

    @Operation(
            summary = "Update reservation",
            description = "To update existed reservation in the booking service by reservation ID " +
                    "(user with role 'USER' can update only their reservations",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"reservation", "PUT"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = ReservationResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
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
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PutMapping("/{id}")
    public ReservationResponse updateReservation(@PathVariable("id") Long id,
                                                 @RequestBody UpsertReservationRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return reservationService.update(id, request, userDetails.getUsername());
    }

    @Operation(
            summary = "Delete reservation",
            description = "To delete existed reservation by reservation ID " +
                    "(user with role 'USER' can delete only their reservations",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"reservation", "DELETE"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204"
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
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
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.cancel(id, userDetails.getUsername());
    }
}
