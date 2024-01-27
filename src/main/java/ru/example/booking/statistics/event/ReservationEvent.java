package ru.example.booking.statistics.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationEvent {

    private String id;

    private Instant reservationAt;

    private Long userId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Long roomId;
}
