package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.model.Room;
import ru.example.booking.repository.RoomRepository;
import ru.example.booking.service.RoomService;
import ru.example.booking.util.BeanUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Value("${app.dateFormat}")
    private String datePattern;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Room not found, ID is " + id)
        );
    }

    @Override
    public Room findByName(String name) {
        return roomRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Room not found, name is " + name)
        );
    }

    @Override
    public Room updateById(Long id, Room room) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }

        Room existedRoom = findById(id);
        BeanUtils.copyNonNullProperties(room, existedRoom);

        return roomRepository.save(existedRoom);
    }

    @Override
    public Room save(Room room) {
        if (roomRepository.existsByName(room.getName())) {
            throw new EntityAlreadyExists("Room with name \"" + room.getName() + "\" is already exists");
        }
        return roomRepository.save(room);
    }

    @Override
    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }
        roomRepository.deleteById(id);
    }

    @Override
    public Room addBookedDates(Long roomId, String from, String to) {
        return addBookedDates(roomId, strDateToLocalDate(from), strDateToLocalDate(to));
    }

    @Override
    public Room addBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findById(roomId);

        Map<Boolean, String> preValidation = preValidateDates(from, to);
        if (preValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + preValidation.get(false));
        }

        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());
        Set<LocalDate> datesToBeChecked = new TreeSet<>(getDateList(from, to));

        if (!isAvailableDates(existedDates, datesToBeChecked)) {
            throw new RoomBookingException("This dates is unavailable");
        }

        existedDates.addAll(datesToBeChecked);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    @Override
    public Room deleteBookedDates(Long roomId, String from, String to) {
        return deleteBookedDates(roomId, strDateToLocalDate(from), strDateToLocalDate(to));
    }

    @Override
    public Room deleteBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findById(roomId);

        Map<Boolean, String> preValidation = preValidateDates(from, to);
        if (preValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + preValidation.get(false));
        }

        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());
        Set<LocalDate> datesToBeChecked = getDateList(from, to);

        if (!isBookedDates(existedDates, datesToBeChecked)) {
            throw new RoomBookingException("This date/s is not booked");
        }

        existedDates.removeAll(datesToBeChecked);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    @Override
    public boolean isAvailableDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked) {
        return datesToBeChecked.stream().noneMatch(existedDates::contains);
    }

    @Override
    public boolean isBookedDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked) {
        return existedDates.containsAll(datesToBeChecked);
    }

    @Override
    public LocalDate strDateToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new RoomBookingException("Input dates is incorrect");
        }
    }

    @Override
    public Set<LocalDate> getDateList(LocalDate start, LocalDate end) {
        Set<LocalDate> dates = new HashSet<>();
        int iteration = 0;

        do {
            dates.add(start.plusDays(iteration));
            iteration++;
        } while (!dates.contains(end));

        return dates;
    }

    private Map<Boolean, String> preValidateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            return Map.of(false, "Date \"to\" is earlier than date \"from\"");
        }
        if (start.isBefore(LocalDate.now())) {
            return Map.of(false, "Date \"from\" is in the past");
        }
        return Map.of(true, "OK");
    }

}
