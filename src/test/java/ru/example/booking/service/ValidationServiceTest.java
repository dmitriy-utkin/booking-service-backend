package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import ru.example.booking.abstracts.AbstractMainTest;
import ru.example.booking.dto.defaults.ErrorResponse;

public class ValidationServiceTest extends AbstractMainTest {

    @Test
    public void whenCheckTheCredentialsWithAdmin_thenReturnNothing() throws Exception {
        var adminRequester = createUserWithAdminRole(1);
        var userOwner = createUserWithUserRole(3);
        validationService.isValidAction(adminRequester, userOwner);
    }

    @Test
    public void whenCheckTheCredentialsWithUserOwner_thenReturnNothing() throws Exception {
        var userRequester = createUserWithUserRole(3);
        var userOwner = createUserWithUserRole(3);
        validationService.isValidAction(userRequester, userOwner);
    }

    @Test
    public void whenCheckTheCredentialsWithUserNotOwner_thenReturnError() throws Exception {
        var userRequester = createUserWithUserRole(1);
        var userOwner = createUserWithUserRole(3);

        var expectedResult = new ErrorResponse("AccessDenied");

        ErrorResponse actualResult = null;

        try {
            validationService.isValidAction(userRequester, userOwner);
        } catch (AccessDeniedException e) {
            actualResult = new ErrorResponse("AccessDenied");;
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }
}
