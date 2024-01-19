package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.RoomAbstractTest;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.model.Room;
import ru.example.booking.model.RoomDescription;
import ru.example.booking.web.model.defaults.ErrorResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

public class RoomServiceTest extends RoomAbstractTest {

    @Test
    public void whenBookAvailableDates_thenReturnUpdatedRoom() throws Exception {

        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var bookingDayFrom = LocalDate.now();
        var bookingDayFromStr = bookingDayFrom.format(dateFormatter);

        var bookingDayTo = bookingDayFrom.plusDays(1);
        var bookingDayToStr = bookingDayTo.format(dateFormatter);

        var expectedResult = createStandardRoomWithoutBookedDates(1, false);
        var bookedDates = new TreeSet<>(Set.of(
                bookingDayFrom,
                bookingDayTo
        ));
        expectedResult.setBookedDates(bookedDates);

        var actualResult = roomService.addBookedDates(1L, bookingDayFromStr, bookingDayToStr);

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenDeleteBooking_thenReturnUpdatedRoom() throws Exception {
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var bookingDayToBeDeleted = LocalDate.now();
        var bookingDayToBeDeletedStr = bookingDayToBeDeleted.format(dateFormatter);

        var savedRoom = roomService.save(createDefaultRoomWithBookingDatesTodayAndTomorrow(RoomDescription.STANDARD));

        var expectedResult = Room.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .capacity(savedRoom.getCapacity())
                .hotel(savedRoom.getHotel())
                .price(savedRoom.getPrice())
                .number(savedRoom.getNumber())
                .description(savedRoom.getDescription())
                .bookedDates(
                        Set.of(bookingDayToBeDeleted.plusDays(1))
                )
                .build();

        var actualResult = roomService.deleteBookedDates(savedRoom.getId(), bookingDayToBeDeletedStr, bookingDayToBeDeletedStr);

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenDeleteNotExistsBooking_thenReturnError() throws Exception {
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var bookingDayToBeDeleted = LocalDate.now().plusDays(3);
        var bookingDayToBeDeletedStr = bookingDayToBeDeleted.format(dateFormatter);

        var savedRoom = roomService.save(createDefaultRoomWithBookingDatesTodayAndTomorrow(RoomDescription.STANDARD));

        var expectedResult = new ErrorResponse("This date/s is not booked");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.deleteBookedDates(savedRoom.getId(), bookingDayToBeDeletedStr, bookingDayToBeDeletedStr);
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenBookNotAvailableDates_thenReturnError() throws Exception {
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var bookingDay = LocalDate.now();
        var bookingDayStr = bookingDay.format(dateFormatter);

        var savedRoom = roomService.save(createDefaultRoomWithBookingDatesTodayAndTomorrow(RoomDescription.STANDARD));

        var expectedResult = new ErrorResponse("This dates is unavailable");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.addBookedDates(savedRoom.getId(), bookingDayStr, bookingDayStr);
        } catch (RoomBookingException e) {
            actualResult = new ErrorResponse(e.getMessage());
        }

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenBookDatesFromMoreThanTo_thenReturnError() throws Exception {
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var bookingDayFrom = LocalDate.now();
        var bookingDayFromStr = bookingDayFrom.format(dateFormatter);

        var bookingDayTo = LocalDate.now().minusDays(2);
        var bookingDayToStr = bookingDayTo.format(dateFormatter);

        var expectedResult = new ErrorResponse("Dates is incorrect: Date \"to\" is earlier than date \"from\"");

        ErrorResponse actualResult = new ErrorResponse();

        try {
            roomService.addBookedDates(1L, bookingDayFromStr, bookingDayToStr);
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
