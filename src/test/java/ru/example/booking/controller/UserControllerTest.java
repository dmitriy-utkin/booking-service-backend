package ru.example.booking.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.UserAbstractTest;
import ru.example.booking.dao.RoleType;
import ru.example.booking.dao.User;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UpdateUserRequest;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends UserAbstractTest {

    @Test
    public void whenCreateNewUserOnPublicControllerWithoutLogging_thenReturnNewUser() throws Exception {

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

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindAllUsers_thenReturnOkResponse() throws Exception {

        JsonAssert.assertJsonEquals(5L, userRepository.count());

        var expectedResponse = userMapper.userListToResponseList(createDefaultUserList(3, 2));

        var actualResponse = mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @Test
    public void whenFindAllWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void whenFindByIdWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void whenFindByUsernameWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(get("/api/user/username/user1"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenFindUserById_theReturnOkResponse() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsById(1L));
        JsonAssert.assertJsonEquals(false, userRepository.existsById(6L));

        var expectedResponse = userMapper.userToUserResponse(createUserWithUserRole(1));

        var actualResponse = mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenFindUserByOwnerIdWithoutAdmin_theReturnOkResponse() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsById(1L));
        JsonAssert.assertJsonEquals(false, userRepository.existsById(6L));

        var expectedResponse = userMapper.userToUserResponse(createUserWithUserRole(2));

        var actualResponse = mockMvc.perform(get("/api/user/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenFindUserByNoRequesterId_theReturnError() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsById(1L));
        JsonAssert.assertJsonEquals(false, userRepository.existsById(6L));

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(get("/api/user/2"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @Test
    @WithMockUser(username = "user5")
    public void whenFindUserByUsername_thenReturnOkResponse() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsByUsername("user5"));
        JsonAssert.assertJsonEquals(false, userRepository.existsByUsername("user6"));

        var expectedResponse = userMapper.userToUserResponse(createUserWithAdminRole(5));

        var actualResponse = mockMvc.perform(get("/api/user/username/user5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenFindUserByOwnerUsernameWithoutAdmin_thenReturnOkResponse() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsByUsername("user5"));
        JsonAssert.assertJsonEquals(false, userRepository.existsByUsername("user6"));

        var expectedResponse = userMapper.userToUserResponse(createUserWithUserRole(1));

        var actualResponse = mockMvc.perform(get("/api/user/username/user1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenFindUserByNotOwnerUsername_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(true, userRepository.existsByUsername("user5"));
        JsonAssert.assertJsonEquals(false, userRepository.existsByUsername("user6"));

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(get("/api/user/username/user5"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCreateNewUser_thenReturnNewUserAndIncreaseUserRepository() throws Exception {

        JsonAssert.assertJsonEquals(5L, userRepository.count());

        var request = CreateUserRequest.builder()
                .username("New user")
                .email("newUser@email.com")
                .password("pass")
                .build();

        mockMvc.perform(post("/api/user?role=ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());


        JsonAssert.assertJsonEquals(6L, userRepository.count());
    }

    @Test
    public void whenCreateNewUserWithoutLogging_thenReturnError() throws Exception {

        var request = CreateUserRequest.builder()
                .username("New user")
                .email("newUser@email.com")
                .password("pass")
                .build();

        mockMvc.perform(post("/api/user?role=ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(5L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenCreateNewUserByRoleUser_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, userRepository.count());

        var request = CreateUserRequest.builder()
                .username("New user")
                .email("newUser@email.com")
                .password("pass")
                .build();

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(post("/api/user?role=ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(5L, userRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCreateNewUserWithWrongRole_thenReturnError() throws Exception {

        var request = CreateUserRequest.builder()
                .username("New user")
                .email("newUser@email.com")
                .password(passwordEncoder.encode("pass"))
                .build();

        mockMvc.perform(post("/api/user?role=ROLE_NON")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        JsonAssert.assertJsonEquals(5L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenUpdateUser_thenReturnUpdatedUser() throws Exception {

        String newUsername1 = "newUsername";
        String newUsername2 = "newUsername2";
        String newPassword = "new_pass";
        String newEmail1 = "newEmail1@email.com";
        String newEmail2 = "newEmail2@email.com";
        var newRoles = Set.of(RoleType.ROLE_USER);

        var request1 = UpdateUserRequest.builder()
                .username(newUsername1)
                .build();

        var request2 = UpdateUserRequest.builder()
                .email(newEmail1)
                .build();

        var request3 = UpdateUserRequest.builder()
                .username(newUsername2)
                .password(newPassword)
                .email(newEmail2)
                .roles(newRoles)
                .build();

        var expectedResponse1 = userMapper.userToUserResponse(createUserWithUserRole(1));
        expectedResponse1.setUsername(newUsername1);

        var expectedResponse2 = userMapper.userToUserResponse(createUserWithUserRole(2));
        expectedResponse2.setEmail(newEmail1);

        var expectedResponse3 = userMapper.userToUserResponse(createUserWithAdminRole(5));
        expectedResponse3.setUsername(newUsername2);
        expectedResponse3.setEmail(newEmail2);
        expectedResponse3.setRoles(newRoles);

        var actualResponse1 = mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse2 = mockMvc.perform(put("/api/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse3 = mockMvc.perform(put("/api/user/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, userRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse1, actualResponse1);
        JsonAssert.assertJsonEquals(expectedResponse2, actualResponse2);
        JsonAssert.assertJsonEquals(expectedResponse3, actualResponse3);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateUserByOwnerUser_thenReturnUpdatedUser() throws Exception {

        String newUsername = "newUsername";

        var request = UpdateUserRequest.builder()
                .username(newUsername)
                .build();

        var expectedResponse = userMapper.userToUserResponse(createUserWithUserRole(1));
        expectedResponse.setUsername(newUsername);

        var actualResponse1 = mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, userRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse1);
    }

    @Test
    public void whenUpdateUserWithoutLogging_thenReturnError() throws Exception {

        String newUsername = "newUsername";

        var request = UpdateUserRequest.builder()
                .username(newUsername)
                .build();

        mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "user2")
    public void whenUpdateUserByNotOwnerUser_thenReturnError() throws Exception {

        String newUsername = "newUsername";

        var request = UpdateUserRequest.builder()
                .username(newUsername)
                .build();

        var expectedResponse = getAccessErrorResponse();

        var actualResponse1 = mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, userRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse1);
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenDeleteUserById_thenReturnNoContentAndDecreaseUserRepository() throws Exception {
        JsonAssert.assertJsonEquals(5L, userRepository.count());

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(4L, userRepository.count());
    }

    @Test
    public void whenDeleteUserByIdWithoutLogging_thenReturnError() throws Exception {
        JsonAssert.assertJsonEquals(5L, userRepository.count());

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenDeleteUserByOwnUser_thenReturnNoContentAndDecreaseUserRepository() throws Exception {
        JsonAssert.assertJsonEquals(5L, userRepository.count());

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(4L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "user2")
    public void whenDeleteUserByNotOwnUser_thenReturnError() throws Exception {
        JsonAssert.assertJsonEquals(5L, userRepository.count());

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, userRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindUserByNotExistId_thenReturnError() throws Exception {
        var expectedResponse = new ErrorResponse("User not found, ID is 100");

        var actualResponse = mockMvc.perform(get("/api/user/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindUserByNotExistUsername_thenReturnError() throws Exception {
        var expectedResponse = new ErrorResponse("User not found, username is 100");

        var actualResponse = mockMvc.perform(get("/api/user/username/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateUserByNotExistId_thenReturnError() throws Exception {
        var expectedResponse = new ErrorResponse("User not found, ID is 100");


        var actualResponse = mockMvc.perform(get("/api/user/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                UpdateUserRequest.builder().username("someUsername").build()
                        )))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDeleteUserByNotExistId_thenReturnError() throws Exception {
        var expectedResponse = new ErrorResponse("User not found, ID is 100");

        var actualResponse = mockMvc.perform(delete("/api/user/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }
}
