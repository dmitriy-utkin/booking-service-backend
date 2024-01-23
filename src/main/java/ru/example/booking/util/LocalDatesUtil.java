package ru.example.booking.util;

import lombok.experimental.UtilityClass;
import ru.example.booking.exception.RoomBookingException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class LocalDatesUtil {

    public String localDateToStr(LocalDate date, String datePattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        try {
            return date.format(formatter);
        } catch (DateTimeParseException e) {
            throw new RoomBookingException("Input dates is incorrect");
        }
    }

    public LocalDate strDateToLocalDate(String date, String datePattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new RoomBookingException("Input dates is incorrect");
        }
    }
}
