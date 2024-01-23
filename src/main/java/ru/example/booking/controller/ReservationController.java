package ru.example.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.ReservationResponseList;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.service.ReservationService;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationResponseList findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return reservationService.findById(id, userDetails.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReservation(@RequestBody UpsertReservationRequest request,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.booking(request, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public ReservationResponse updateReservation(@PathVariable("id") Long id,
                                                 @RequestBody UpsertReservationRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return reservationService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.cancel(id, userDetails.getUsername());
    }
}
