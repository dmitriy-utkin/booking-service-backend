package ru.example.booking.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.ReservationMapper;
import ru.example.booking.service.ReservationService;
import ru.example.booking.web.model.reservation.ReservationResponse;
import ru.example.booking.web.model.reservation.ReservationResponseList;
import ru.example.booking.web.model.reservation.UpsertReservationRequest;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ReservationMapper reservationMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseList> findAll() {
        return ResponseEntity.ok(reservationMapper.reservationListToResponseList(reservationService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> findById(@PathVariable("id") Long id,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationMapper.reservationToResponse(
                reservationService.findById(id, userDetails.getUsername()))
        );
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody UpsertReservationRequest request,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reservationMapper.reservationToResponse(
                        reservationService.booking(reservationMapper.requestToReservation(request), userDetails.getUsername())
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable("id") Long id,
                                                                 @RequestBody UpsertReservationRequest request,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                reservationMapper.reservationToResponse(
                        reservationService.update(
                                id, reservationMapper.requestToReservation(request), userDetails.getUsername()
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.cancel(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
