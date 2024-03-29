package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.booking.configuration.properties.CacheProperties;
import ru.example.booking.dao.postrgres.Reservation;
import ru.example.booking.dao.postrgres.Room;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.SimpleRoomResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.mapper.RoomMapper;
import ru.example.booking.repository.postgres.RoomRepository;
import ru.example.booking.repository.postgres.RoomSpecification;
import ru.example.booking.util.BeanUtils;
import ru.example.booking.util.LocalDatesUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    private final HotelService hotelService;

    private final RoomMapper roomMapper;

    @Value("${app.dateFormat}")
    private String datePattern;

    @Cacheable(cacheNames = CacheProperties.CacheNames.ALL_ROOMS)
    public RoomResponseList findAll() {
        return roomMapper.roomListToResponseList(roomRepository.findAll());
    }

    @Cacheable(cacheNames = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, key = "#settings")
    public RoomResponseList findAll(FindAllSettings settings) {

        if (settings.getRoomFilter().getCheckInDate() != null && settings.getRoomFilter().getCheckOutDate() != null) {
            settings.getRoomFilter().setCheckInLocalDate(LocalDatesUtil.strDateToLocalDate(settings.getRoomFilter().getCheckInDate(), datePattern));
            settings.getRoomFilter().setCheckOutLocalDate(LocalDatesUtil.strDateToLocalDate(settings.getRoomFilter().getCheckOutDate(), datePattern));
        }

        return roomMapper.roomListToResponseList(
                roomRepository.findAll(RoomSpecification.withFilter(settings.getRoomFilter()),
                        PageRequest.of(settings.getPageNum(), settings.getPageSize())).getContent()
        );
    }

    @Cacheable(cacheNames = CacheProperties.CacheNames.ROOM_BY_ID, key = "#id")
    public SimpleRoomResponse findById(Long id) {
        return roomMapper.roomToSimpleResponse(findRoomById(id));
    }

    public Room findRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Room not found, ID is " + id)
        );
    }

    public SimpleRoomResponse findByName(String name) {
        var room = roomRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Room not found, name is " + name)
        );
        return roomMapper.roomToSimpleResponse(room);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ROOM_BY_ID, allEntries = true)
    })
    public SimpleRoomResponse updateById(Long id, UpsertRoomRequest request) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }

        validateRoomName(request.getName());

        Room existedRoom = findRoomById(id);
        Room updatedRoom = roomMapper.requestToRoom(request);
        BeanUtils.copyNonNullProperties(updatedRoom, existedRoom);

        return roomMapper.roomToSimpleResponse(roomRepository.save(existedRoom));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, allEntries = true)
    })
    public SimpleRoomResponse save(UpsertRoomRequest request) {
        validateRoomName(request.getName());

        var room = roomMapper.requestToRoom(request);
        hotelService.addRoom(room);
        return roomMapper.roomToSimpleResponse(roomRepository.save(room));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ROOM_BY_ID, allEntries = true)
    })
    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }
        roomRepository.deleteById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ROOM_BY_ID, allEntries = true)
    })
    public SimpleRoomResponse addReservation(Reservation reservation) {
        Room existedRoom = findRoomById(reservation.getRoom().getId());

        Map<Boolean, String> preValidation = preValidateDates(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (preValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + preValidation.get(false));
        }

        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());
        Set<LocalDate> datesToBeChecked = new TreeSet<>(getDateList(reservation.getCheckInDate(), reservation.getCheckOutDate()));

        if (!isAvailableDates(existedDates, datesToBeChecked)) {
            throw new RoomBookingException("This dates is unavailable");
        }

        existedDates.addAll(datesToBeChecked);

        var existedReservations = existedRoom.getReservations();
        existedReservations.add(reservation);

        existedRoom.setReservations(existedReservations);
        existedRoom.setBookedDates(existedDates);
        return roomMapper.roomToSimpleResponse(roomRepository.save(existedRoom));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ALL_ROOMS_WITH_FILTER, allEntries = true),
            @CacheEvict(value = CacheProperties.CacheNames.ROOM_BY_ID, allEntries = true)
    })
    public SimpleRoomResponse deleteReservation(Reservation reservation) {
        Room existedRoom = findRoomById(reservation.getRoom().getId());

        Map<Boolean, String> preValidation = preValidateDates(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (preValidation.containsKey(false)) {
            throw new RoomBookingException("Dates is incorrect: " + preValidation.get(false));
        }

        Set<LocalDate> existedDates = new TreeSet<>(existedRoom.getBookedDates());
        Set<LocalDate> datesToBeChecked = getDateList(reservation.getCheckInDate(), reservation.getCheckOutDate());

        if (!isBookedDates(existedDates, datesToBeChecked)) {
            throw new RoomBookingException("This date/s is not booked");
        }

        existedDates.removeAll(datesToBeChecked);

        var existedReservation = existedRoom.getReservations();
        existedReservation.remove(reservation);

        existedRoom.setReservations(existedReservation);
        existedRoom.setBookedDates(existedDates);
        return roomMapper.roomToSimpleResponse(roomRepository.save(existedRoom));
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

    private void validateRoomName(String name) {
        if (roomRepository.existsByName(name)) {
            throw new EntityAlreadyExists("Room with name \"" + name + "\" is already exists");
        }
    }
}
