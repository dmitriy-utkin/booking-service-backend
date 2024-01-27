package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.RoomAbstractTest;
import ru.example.booking.dao.postrgres.Reservation;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dao.postrgres.RoomDescription;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.exception.RoomBookingException;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public class RoomServiceTest extends RoomAbstractTest {

    @Test
    public void whenBookAvailableDates_thenReturnUpdatedRoom() throws Exception {

        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        var bookingDayFrom = LocalDate.now();

        var bookingDayTo = bookingDayFrom.plusDays(1);

        var room = createStandardRoomWithoutBookedDates(1, false);
        var bookedDates = new TreeSet<>(Set.of(
                bookingDayFrom,
                bookingDayTo
        ));
        room.setBookedDates(bookedDates);

        var expectedResponse = roomMapper.roomToSimpleResponse(room);

        var actualResult = roomService.addReservation(Reservation.builder()
                .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .checkInDate(bookingDayFrom)
                .checkOutDate(bookingDayTo)
                .build());

        JsonAssert.assertJsonEquals(expectedResponse, actualResult);
    }

    @Test
    public void whenDeleteNotExistsBooking_thenReturnError() throws Exception {
        var bookingDayToBeDeleted = LocalDate.now().plusDays(3);

        var expectedResult = new ErrorResponse("This date/s is not booked");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.deleteReservation(Reservation.builder()
                    .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                    .user(createDefaultUser(1, RoleType.ROLE_USER))
                    .checkInDate(bookingDayToBeDeleted)
                    .checkOutDate(bookingDayToBeDeleted)
                    .build());
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenBookNotAvailableDates_thenReturnError() throws Exception {

        var bookingDay = LocalDate.now();

        var wrongReservation = Reservation.builder()
                .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                .user(createDefaultUser(1, RoleType.ROLE_USER))
                .checkInDate(bookingDay)
                .checkOutDate(bookingDay)
                .build();

        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        roomService.addReservation(wrongReservation);

        var expectedResult = new ErrorResponse("This dates is unavailable");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.addReservation(wrongReservation);
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenBookDatesFromMoreThanTo_thenReturnError() throws Exception {

        var bookingDayFrom = LocalDate.now();

        var bookingDayTo = LocalDate.now().minusDays(2);

        var expectedResult = new ErrorResponse("Dates is incorrect: Date \"to\" is earlier than date \"from\"");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.addReservation(Reservation.builder()
                    .room(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
                    .user(createDefaultUser(1, RoleType.ROLE_USER))
                    .checkInDate(bookingDayFrom)
                    .checkOutDate(bookingDayTo)
                    .build());
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenCheckForAvailabilityDates_thenReturnTrue() {

        var datesToBeChecked = roomService.getDateList(LocalDate.now(), LocalDate.now().plusDays(10));

        var existedDates = roomService.getDateList(LocalDate.now().plusDays(11),
                LocalDate.now().plusDays(15));

        var actualResult = roomService.isAvailableDates(existedDates, datesToBeChecked);

        JsonAssert.assertJsonEquals(true, actualResult);
    }

    @Test
    public void whenCheckForAvailabilityDates_thenReturnFalse() {

        var datesToBeChecked = roomService.getDateList(LocalDate.now(), LocalDate.now().plusDays(10));

        var existedDates = roomService.getDateList(LocalDate.now().plusDays(9),
                LocalDate.now().plusDays(15));

        var actualResult = roomService.isAvailableDates(existedDates, datesToBeChecked);

        JsonAssert.assertJsonEquals(false, actualResult);
    }

    @Test
    public void whenCheckIsBookedDates_thenReturnTrue() {

        var datesToBeChecked = roomService.getDateList(LocalDate.now().plusDays(9),
                LocalDate.now().plusDays(10));

        var existedDates = roomService.getDateList(LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(12));

        var actualResult = roomService.isBookedDates(existedDates, datesToBeChecked);

        JsonAssert.assertJsonEquals(true, actualResult);
    }

    @Test
    public void whenCheckIsBookedDates_thenReturnFalse() {

        var datesToBeChecked = roomService.getDateList(LocalDate.now().plusDays(9),
                LocalDate.now().plusDays(15));

        var existedDates = roomService.getDateList(LocalDate.now(),
                LocalDate.now().plusDays(12));

        var actualResult = roomService.isBookedDates(existedDates, datesToBeChecked);

        JsonAssert.assertJsonEquals(false, actualResult);
    }
}
