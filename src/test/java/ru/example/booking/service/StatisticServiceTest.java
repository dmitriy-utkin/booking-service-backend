package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.AbstractMainTest;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dto.user.CreateUserRequest;

public class StatisticServiceTest extends AbstractMainTest {

    @Test
    public void whenSaveNewUser_thenIncreaseMongoUserCollection() {

        JsonAssert.assertJsonEquals(0L, statisticUserRepository.count());

        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                        .username("New user")
                        .password("pass")
                        .email("newUser@email.com")
                .build());

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonAssert.assertJsonEquals(1L, statisticUserRepository.count());
    }
}
