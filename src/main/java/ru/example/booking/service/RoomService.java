package ru.example.booking.service;

import ru.example.booking.model.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface RoomService {

    List<Room> findAll();

    Room findById(Long id);

    Room findByName(String name);

    Room updateById(Long id, Room room);

    Room save(Room room);

    void deleteById(Long id);

    Room addBookedDates(Long roomId, String from, String to);

    Room addBookedDates(Long roomId, LocalDate from, LocalDate to);

    Room deleteBookedDates(Long roomId, String from, String to);

    Room deleteBookedDates(Long roomId, LocalDate from, LocalDate to);

    boolean isAvailableDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked);

    boolean isBookedDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked);

    LocalDate strDateToLocalDate(String date);

    Set<LocalDate> getDateList(LocalDate start, LocalDate end);
}
