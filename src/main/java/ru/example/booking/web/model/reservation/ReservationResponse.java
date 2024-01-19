package ru.example.booking.web.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;

    private String checkInDate;

    private String checkOutDate;

    private Long roomId;

    private Long userId;

    private String userEmail;
}
