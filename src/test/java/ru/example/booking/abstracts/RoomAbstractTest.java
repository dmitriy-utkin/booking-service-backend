package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;

public class RoomAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {

        resetSequence();

        createDefaultRoomList(true).forEach(
                roomRepository::save
        );
    }
}
