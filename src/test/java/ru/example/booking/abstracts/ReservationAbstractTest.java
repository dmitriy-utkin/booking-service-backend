package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.util.LocalDatesUtil;

public class ReservationAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {
        resetSequence();

        createDefaultReservationListWithStepByCounter(5, true).forEach(
                reservation -> reservationService.booking(UpsertReservationRequest.builder()
                                .roomId(reservation.getId())
                                .checkInDate(LocalDatesUtil.localDateToStr(reservation.getCheckInDate(), DATE_PATTERN))
                                .checkOutDate(LocalDatesUtil.localDateToStr(reservation.getCheckOutDate(), DATE_PATTERN))
                        .build(), reservation.getUser().getUsername())
        );
    }
}
