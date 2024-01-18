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
        Room existedRoom = findById(roomId);
        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());

        var start = strDateToLocalDate(from);
        var end = strDateToLocalDate(to);

        Set<LocalDate> preparedDates = prepareDate(existedDates, start, end, false);
        existedDates.addAll(preparedDates);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    @Override
    public Room addBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findById(roomId);
        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());

        Set<LocalDate> preparedDates = prepareDate(existedDates, from, to, false);
        existedDates.addAll(preparedDates);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    @Override
    public Room deleteBookedDates(Long roomId, String from, String to) {
        Room existedRoom = findById(roomId);
        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());

        var start = strDateToLocalDate(from);
        var end = strDateToLocalDate(to);

        Set<LocalDate> preparedDates = prepareDate(existedDates, start, end, true);
        existedDates.removeAll(preparedDates);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    @Override
    public Room deleteBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findById(roomId);
        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());

        Set<LocalDate> preparedDates = prepareDate(existedDates, from, to, true);
        existedDates.removeAll(preparedDates);
        existedRoom.setBookedDates(existedDates);
        return existedRoom;
    }

    private LocalDate strDateToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new RoomBookingException("Input dates is incorrect");
        }
    }

    private Set<LocalDate> prepareDate(Set<LocalDate> currentDates, LocalDate start, LocalDate end, boolean removing) {


        Map<Boolean, String> preValidation = preValidateDates(start, end);

        if (preValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + preValidation.get(false));
        }

        Set<LocalDate> dates = new HashSet<>();
        int iteration = 0;

        do {
            dates.add(start.plusDays(iteration));
            iteration++;
        } while (!dates.contains(end));

        Map<Boolean, String> postValidation = postValidateDates(dates, currentDates, removing);

        if (postValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + postValidation.get(false));
        }

        return dates;
    }

    private Map<Boolean, String> postValidateDates(Set<LocalDate> newDates,
                                                   Set<LocalDate> currentDates,
                                                   boolean removing) {
        Set<LocalDate> tempSet = new HashSet<>(Set.copyOf(newDates));
        tempSet.retainAll(currentDates);
        int initialSize = newDates.size();

        if (!removing && !tempSet.isEmpty()) {
            return Map.of(false, "This dates is unavailable");
        }
        if (removing && tempSet.size() != initialSize) {
            return Map.of(false, "This date/s is not booked");
        }
        return Map.of(true, "OK");
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
