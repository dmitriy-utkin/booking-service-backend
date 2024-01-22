package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;

public class HotelAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {

        resetSequence();

        for (int i = 1; i <= 5; i++) {
            hotelRepository.save(createDefaultHotel(i));
        }
    }
}
