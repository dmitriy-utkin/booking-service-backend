package ru.example.booking.web.model.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertReservationRequest {

    @NotBlank
    private String checkInDate;

    @NotBlank
    private String checkOutDate;

    @NotNull
    private Long roomId;
}
