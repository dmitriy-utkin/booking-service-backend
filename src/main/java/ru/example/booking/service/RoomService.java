package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.Room;
import ru.example.booking.dto.room.RoomResponse;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.mapper.RoomMapper;
import ru.example.booking.repository.RoomRepository;
import ru.example.booking.util.BeanUtils;
import ru.example.booking.util.LocalDatesUtil;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    @Value("${app.dateFormat}")
    private String datePattern;

    public RoomResponseList findAll() {
        return roomMapper.roomListToResponseList(roomRepository.findAll());
    }

    public RoomResponse findById(Long id) {
        return roomMapper.roomToResponse(findRoomById(id));
    }

    public Room findRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Room not found, ID is " + id)
        );
    }

    public RoomResponse findByName(String name) {
        var room = roomRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Room not found, name is " + name)
        );
        return roomMapper.roomToResponse(room);
    }

    public RoomResponse updateById(Long id, UpsertRoomRequest request) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }

        Room existedRoom = findRoomById(id);
        Room updatedRoom = roomMapper.requestToRoom(request);
        BeanUtils.copyNonNullProperties(updatedRoom, existedRoom);

        return roomMapper.roomToResponse(roomRepository.save(existedRoom));
    }

    public RoomResponse save(UpsertRoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new EntityAlreadyExists("Room with name \"" + request.getName() + "\" is already exists");
        }
        return roomMapper.roomToResponse(roomRepository.save(roomMapper.requestToRoom(request)));
    }

    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }
        roomRepository.deleteById(id);
    }

    public RoomResponse addBookedDates(Long roomId, String from, String to) {
        return addBookedDates(roomId, LocalDatesUtil.strDateToLocalDate(from, datePattern),
                LocalDatesUtil.strDateToLocalDate(to, datePattern));
    }

    public RoomResponse addBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findRoomById(roomId);

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
        return roomMapper.roomToResponse(existedRoom);
    }

    public RoomResponse deleteBookedDates(Long roomId, String from, String to) {
        return deleteBookedDates(roomId, LocalDatesUtil.strDateToLocalDate(from, datePattern),
                LocalDatesUtil.strDateToLocalDate(to, datePattern));
    }

    public RoomResponse deleteBookedDates(Long roomId, LocalDate from, LocalDate to) {
        Room existedRoom = findRoomById(roomId);

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
        return roomMapper.roomToResponse(existedRoom);
    }

    public boolean isAvailableDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked) {
        return datesToBeChecked.stream().noneMatch(existedDates::contains);
    }

    public boolean isBookedDates(Set<LocalDate> existedDates, Set<LocalDate> datesToBeChecked) {
        return existedDates.containsAll(datesToBeChecked);
    }


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
