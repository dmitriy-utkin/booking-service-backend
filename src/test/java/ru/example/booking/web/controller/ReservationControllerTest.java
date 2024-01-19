package ru.example.booking.web.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.ReservationAbstractTest;
import ru.example.booking.web.model.defaults.ErrorResponse;
import ru.example.booking.web.model.reservation.ReservationResponse;
import ru.example.booking.web.model.reservation.UpsertReservationRequest;

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
                createDefaultReservationListWithStepByCounter(5, false)
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCreateNewReservationForAvailableDates_thenReturnReservationAndIncreaseReservationRepo()
            throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var request = UpsertReservationRequest.builder()
                .userId(1L)
                .roomId(1L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now().plusDays(3)))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
                .build();

        var expectedResponse = ReservationResponse.builder()
                .id(6L)
                .userId(1L)
                .roomId(1L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now().plusDays(3)))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
                .userId(1L)
                .userEmail("user1@email.com")
                .build();

        var actualResponse = mockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(6L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCreateNewReservationForUnavailableDates_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var request = UpsertReservationRequest.builder()
                .userId(1L)
                .roomId(1L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now()))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateReservationForAvailableDates_thenReturnResponse() throws Exception {

        var request = UpsertReservationRequest.builder()
                .userId(1L)
                .roomId(1L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
                .build();

        var expectedResponse = ReservationResponse.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
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

        var actualRoomResult = roomService.findById(1L);

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateReservationForUnavailableDates_thenReturnError() throws Exception {

        var request = UpsertReservationRequest.builder()
                .userId(1L)
                .roomId(2L)
                .checkInDate(reservationService.localDateToStr(LocalDate.now()))
                .checkOutDate(reservationService.localDateToStr(LocalDate.now().plusDays(5)))
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenCancelReservation_thenReturnEmptyDatesRoomAndDecreaseReservationRepository() throws Exception {

        JsonAssert.assertJsonEquals(5L, reservationRepository.count());

        var expectedRoomResult = createStandardRoomWithoutBookedDates(1, false);

        mockMvc.perform(delete(("/api/reservation/1")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualRoomResult = roomService.findById(1L);

        JsonAssert.assertJsonEquals(4L, reservationRepository.count());
        JsonAssert.assertJsonEquals(expectedRoomResult, actualRoomResult);
    }
}
