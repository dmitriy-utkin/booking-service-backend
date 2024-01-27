package ru.example.booking.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.example.booking.dao.postrgres.*;
import ru.example.booking.dto.defaults.UploadObject;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.repository.postgres.HotelRepository;
import ru.example.booking.repository.postgres.ReservationRepository;
import ru.example.booking.repository.postgres.RoomRepository;
import ru.example.booking.repository.postgres.UserRepository;
import ru.example.booking.service.ReservationService;
import ru.example.booking.util.LocalDatesUtil;
import ru.example.booking.util.UploadUtils;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Profile("dev")
@Slf4j
public class DevUploader {

    private final ReservationService reservationService;

    private final HotelRepository hotelRepository;

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;

    private final UserRepository userRepository;

    @Value("${app.dateFormat}")
    private String datePattern;


    @Value("${app.uploading.mockHotelPath}")
    private String mockHotelPath;

    @EventListener(ApplicationStartedEvent.class)
    @Order(1)
    public void uploadHotel() {

        clearDatabase();

        UploadObject uploadObject = UploadUtils.readValues(mockHotelPath);

        List<Hotel> savedHotels = hotelRepository.saveAllAndFlush(uploadObject.getHotels());

        List<RoomDescription> descriptions = new ArrayList<>(List.of(
                RoomDescription.SUITE,
                RoomDescription.PRESIDENT,
                RoomDescription.STANDARD,
                RoomDescription.SUPERIOR
        ));

        List<Room> uploadedRooms = uploadObject.getRoomNames().stream().map(
                name -> {
                    Collections.shuffle(savedHotels);
                    Collections.shuffle(descriptions);
                    return Room.builder()
                            .hotel(savedHotels.get(0))
                            .description(descriptions.get(0))
                            .price(BigDecimal.valueOf(generateRandomInt(3_500, 23_000)))
                            .capacity(generateRandomInt(1, 7))
                            .number(generateRandomInt(1, 499))
                            .name(name)
                            .build();
                }
        ).toList();

        List<Room> savedRooms = roomRepository.saveAll(uploadedRooms);

        List<User> savedUsers = userRepository.saveAll(generateUsers(100));

        generateReservations(1_500, savedRooms, uploadObject.getDates()).forEach(
                reservation -> {
                    Collections.shuffle(savedUsers);
                    try {
                        reservationService.booking(reservation, savedUsers.get(0).getUsername());
                    } catch (RoomBookingException e) {
                        log.error(e.getMessage());
                    }
                }
        );
        log.info("Was saved: users - {}, hotels - {}, rooms - {}, reservations - {}",
                savedUsers.size(),
                savedHotels.size(),
                savedRooms.size(),
                reservationRepository.count());
    }

    private Integer generateRandomInt(int min, int max) {
        return new Random().ints(min, max).limit(1).boxed().toList().get(0);
    }

    private List<UpsertReservationRequest> generateReservations(int count, List<Room> rooms, List<String> dates) {
        List<UpsertReservationRequest> reservations = new ArrayList<>();

        while (reservations.size() < count) {
            Collections.shuffle(rooms);
            Collections.shuffle(dates);
            String checkInDate = dates.get(0);
            String checkOutDate = LocalDatesUtil.localDateToStr(
                    LocalDatesUtil.strDateToLocalDate(
                            checkInDate, datePattern
                    ).plusDays(generateRandomInt(0, 14)), datePattern
            );

            reservations.add(UpsertReservationRequest.builder()
                            .roomId(rooms.get(0).getId())
                            .checkInDate(checkInDate)
                            .checkOutDate(checkOutDate)
                    .build());
        }

        return reservations;
    }

    private List<User> generateUsers(int count) {

        List<User> users = new ArrayList<>();
        List<RoleType> roles = new ArrayList<>(List.of(RoleType.ROLE_ADMIN, RoleType.ROLE_USER));

        for (int i = 1; i <= count; i++) {
            Collections.shuffle(roles);
            users.add(User.builder()
                    .email("email" + i + "@email.com")
                    .password("pass" + i)
                    .username("user" + i)
                    .roles(Set.of(roles.get(0)))
                    .build());
        }

        return users;
    }

    private void clearDatabase() {
        userRepository.deleteAll();
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
    }

}
