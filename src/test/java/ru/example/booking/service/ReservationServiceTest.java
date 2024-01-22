package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.ReservationAbstractTest;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dao.RoleType;
import ru.example.booking.dao.RoomDescription;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.util.LocalDatesUtil;

import java.time.LocalDate;
import java.util.TreeSet;

public class ReservationServiceTest extends ReservationAbstractTest {

    @Test
    public void whenBookingForAvailableDates_thenReturnReservation() {

        var reservationRequest = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                .build();

        var expectedResultId = 6L;

        var actualResult = reservationService.booking(reservationRequest, "user1").getId();

        JsonAssert.assertJsonEquals(expectedResultId, actualResult);
    }

    @Test
    public void whenBookingForUnavailableDates_thenReturnError() {

        roomService.addBookedDates(1L, LocalDate.now().plusDays(10), LocalDate.now().plusDays(11));

        var reservationRequest = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                .build();

        var expectedResultId = new ErrorResponse("This dates is unavailable");

        ErrorResponse actualResult = null;
        try {
            reservationService.booking(reservationRequest, "user1");
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResultId, actualResult);
    }

    @Test
    public void whenCancelBookedDates_thenReturnsEmptyBookedDatesListByRoom() {

        var expectedResult = true;

        reservationService.cancel(1L, "user1");

        var actualResult = roomService.findRoomById(1L).getBookedDates().isEmpty();

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenUpdateReservationForAvailableDates_thenReturnReservation() {

        var updatedReservation = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                .build();

        var room = createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false);
        room.setBookedDates(
                new TreeSet<>(roomService.getDateList(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15)))
        );

        var expectedResult = reservationMapper.reservationToResponse(Reservation.builder()
                .id(1L)
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(room)
                .checkInDate(LocalDate.now().plusDays(10))
                .checkOutDate(LocalDate.now().plusDays(15))
                .build(), DATE_PATTERN
        );

        var actualResult = reservationService.update(1L, updatedReservation, "user1");

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenUpdateReservationForUnavailableDates_thenReturnError() {

        var updatedReservation = UpsertReservationRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                .build();

        var expectedResult = new ErrorResponse("This dates in unavailable");

        ErrorResponse actualResponse = null;

        try {
            reservationService.update(1L, updatedReservation, "user1");
        } catch (RoomBookingException e) {
            actualResponse = new ErrorResponse("This dates in unavailable");
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResponse);
    }
}
