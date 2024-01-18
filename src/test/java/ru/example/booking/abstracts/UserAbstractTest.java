package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;

public class UserAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {

        resetSequence();

        userRepository.saveAll(createDefaultUserList(3, 2));

    }
}
