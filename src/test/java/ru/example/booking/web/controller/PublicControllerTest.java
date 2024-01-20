package ru.example.booking.web.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;
import ru.example.booking.web.model.user.CreateUserRequest;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicControllerTest extends UserControllerTest {

    @Test
    public void whenCreateNewUserWithoutLogging_thenReturnNewUser() throws Exception {

        JsonAssert.assertJsonEquals(5L, userRepository.count());

        var request = CreateUserRequest.builder()
                .username("New user")
                .email("newUser@email.com")
                .password("pass")
                .build();

        var expectedResponse = userMapper.userToUserResponse(
                User.builder()
                        .id(6L)
                        .username("New user")
                        .email("newUser@email.com")
                        .password("pass")
                        .roles(Set.of(RoleType.ROLE_ADMIN))
                        .build()
        );

        var actualResponse = mockMvc.perform(post("/api/public/account?role=ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(6L, userRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }
}
