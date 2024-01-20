package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.ReservationAbstractTest;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.model.Reservation;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.RoomDescription;
import ru.example.booking.web.model.defaults.ErrorResponse;

import java.time.LocalDate;
import java.util.TreeSet;

public class ReservationServiceTest extends ReservationAbstractTest {

    @Test
    public void whenBookingForAvailableDates_thenReturnReservation() {

        var reservation = Reservation.builder()
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                .checkInDate(LocalDate.now().plusDays(10))
                .checkOutDate(LocalDate.now().plusDays(15))
                .build();

        var expectedResultId = 6L;

        var actualResult = reservationService.booking(reservation, "user1").getId();

        JsonAssert.assertJsonEquals(expectedResultId, actualResult);
    }

    @Test
    public void whenBookingForUnavailableDates_thenReturnError() {

        var reservation = Reservation.builder()
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(15))
                .build();

        var expectedResultId = new ErrorResponse("This dates is unavailable");

        ErrorResponse actualResult = null;
        try {
            reservationService.booking(reservation, "user1");
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResultId, actualResult);
    }

    @Test
    public void whenCancelBookedDates_thenReturnsEmptyBookedDatesListByRoom() {

        var expectedResult = true;

        reservationService.cancel(1L, "user1");

        var actualResult = roomService.findById(1L).getBookedDates().isEmpty();

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenUpdateReservationForAvailableDates_thenReturnReservation() {

        var updatedReservation = Reservation.builder()
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                .checkInDate(LocalDate.now().plusDays(10))
                .checkOutDate(LocalDate.now().plusDays(15))
                .build();

        var room = createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false);
        room.setBookedDates(
                new TreeSet<>(roomService.getDateList(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15)))
                );

        var expectedResult = Reservation.builder()
                .id(1L)
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(room)
                .checkInDate(LocalDate.now().plusDays(10))
                .checkOutDate(LocalDate.now().plusDays(15))
                .build();

        var actualResult = reservationService.update(1L, updatedReservation, "user1");

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenUpdateReservationForUnavailableDates_thenReturnError() {

        var updatedReservation = Reservation.builder()
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .room(createDefaultRoomWithoutBookedDates(2, RoomDescription.STANDARD, false))
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(15))
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
