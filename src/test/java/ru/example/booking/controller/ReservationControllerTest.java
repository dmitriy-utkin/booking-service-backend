package ru.example.booking.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.ReservationAbstractTest;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.util.LocalDatesUtil;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationControllerTest extends ReservationAbstractTest {

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindAllReservation_thenReturnReservations() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var expectedResponse = reservationMapper.reservationListToResponseList(
                createDefaultReservationListWithStepByCounter(5, false), DATE_PATTERN
        );

        var actualResponse = mockMvc.perform(get("/api/reservation"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user")
    public void whenFindAllReservationByUser_thenReturnError() throws Exception {

        var expectedResponse = new ErrorResponse("Access denied, please contact administrator");

        var actualResponse = mockMvc.perform(get("/api/reservation"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenFindById_thenReturnOkResponse() throws Exception {
        var expectedResponse = ReservationResponse.builder()
                .id(1L)
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                .userId(1L)
                .userEmail("user1@email.com")
                .build();

        var actualResponse = mockMvc.perform(get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenFindByIdByOwnerUser_thenReturnOkResponse() throws Exception {
        var expectedResponse = ReservationResponse.builder()
                .id(1L)
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                .userId(1L)
                .userEmail("user1@email.com")
                .build();

        var actualResponse = mockMvc.perform(get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user2")
    public void whenFindByIdByOwnerUser_thenReturnError() throws Exception {
        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(get("/api/reservation/1"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenFindByIdWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(get("/api/reservation/1"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void whenFindAllWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(get("/api/reservation"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void whenDeleteWithoutLogging_thenReturnError() throws Exception {

        mockMvc.perform(delete("/api/reservation/1"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void whenCreateNewReservationWithoutLogging_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        mockMvc.perform(post("/api/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
    }

    @Test
    public void whenUpdateReservationWithoutLogging_thenReturnError() throws Exception {

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();


        mockMvc.perform(put("/api/reservation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
    }

    @Test
    @WithMockUser(username = "user5", roles = {"ADMIN"})
    public void whenCreateNewReservationForAvailableDates_thenReturnReservationAndIncreaseReservationRepo()
            throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        mockMvc.perform(post("/api/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        JsonAssert.assertJsonEquals(6L, reservationRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCreateNewReservationForUnavailableDates_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        var expectedResponse = new ErrorResponse("This dates is unavailable");

        var actualResponse = mockMvc.perform(post("/api/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenUpdateReservationForAvailableDates_thenReturnResponse() throws Exception {

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        var expectedResponse = ReservationResponse.builder()
                .id(1L)
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .userId(1L)
                .userEmail("user1@email.com")
                .build();

        var actualResponse = mockMvc.perform(put("/api/reservation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        var expectedRoomResult = createStandardRoomWithoutBookedDates(1, false);
        expectedRoomResult.setBookedDates(Set.of(LocalDate.now().plusDays(5)));

        var actualRoomResult = roomService.findRoomById(1L);

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateReservationForAvailableDatesByOwnerUser_thenReturnResponse() throws Exception {

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        var expectedResponse = ReservationResponse.builder()
                .id(1L)
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .userId(1L)
                .userEmail("user1@email.com")
                .build();

        var actualResponse = mockMvc.perform(put("/api/reservation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        var expectedRoomResult = createStandardRoomWithoutBookedDates(1, false);
        expectedRoomResult.setBookedDates(Set.of(LocalDate.now().plusDays(5)));

        var actualRoomResult = roomService.findRoomById(1L);

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }

    @Test
    @WithMockUser(username = "user2")
    public void whenUpdateReservationForAvailableDatesByNotOwnerUser_thenReturnError() throws Exception {

        var request = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(put("/api/reservation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenUpdateReservationForUnavailableDates_thenReturnError() throws Exception {

        var request = UpsertReservationRequest.builder()
                .roomId(2L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                .build();

        var expectedResponse = new ErrorResponse("This dates is unavailable");

        var actualResponse = mockMvc.perform(put("/api/reservation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
    }

    @Test
    @WithMockUser(username = "user5")
    public void whenCancelReservation_thenReturnEmptyDatesRoomAndDecreaseReservationRepository() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var expectedRoomResult = createStandardRoomWithoutBookedDates(1, false);

        mockMvc.perform(delete(("/api/reservation/1")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualRoomResult = roomService.findRoomById(1L);

        JsonAssert.assertJsonEquals(4L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenCancelReservationByOwnerUser_thenReturnEmptyDatesRoomAndDecreaseReservationRepository() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var expectedRoomResult = createStandardRoomWithoutBookedDates(1, false);

        mockMvc.perform(delete(("/api/reservation/1")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualRoomResult = roomService.findRoomById(1L);

        JsonAssert.assertJsonEquals(4L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }

    @Test
    @WithMockUser(username = "user2")
    public void whenCancelReservationByNotOwnerUser_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var expectedResponse = getAccessErrorResponse();

        var actualResponse = mockMvc.perform(delete(("/api/reservation/1")))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }
}