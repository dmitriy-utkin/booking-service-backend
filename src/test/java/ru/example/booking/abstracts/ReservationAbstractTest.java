package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;

public class ReservationAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {
        resetSequence();

        createDefaultReservationListWithStepByCounter(5, true).forEach(reservationRepository::save);
    }
}
