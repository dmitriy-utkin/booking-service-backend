package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.model.Reservation;
import ru.example.booking.repository.ReservationRepository;
import ru.example.booking.service.ReservationService;
import ru.example.booking.service.RoomService;
import ru.example.booking.service.UserService;
import ru.example.booking.service.ValidationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final RoomService roomService;

    private final UserService userService;

    private final ValidationService validationService;

    @Value("${app.dateFormat}")
    private String datePattern;

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Long id, String username) {

        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation is not found, ID is " + id)
        );

        if (!validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                reservation.getUser())) {
            throw new AccessDeniedException("Access denied");
        }

        return reservation;
    }

    @Override
    public Reservation booking(Reservation reservation, String username) {
        roomService.addBookedDates(reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate());
        reservation.setUser(userService.findByUsernameWithoutPrivilegeValidation(username));
        return reservationRepository.save(reservation);
    }

    @Override
    public void cancel(Long id, String username) {
        Reservation reservationForRemoving = findById(id, username);

        if (!validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                reservationForRemoving.getUser())) {
            throw new AccessDeniedException("Access denied");
        }

        roomService.deleteBookedDates(id, reservationForRemoving.getCheckInDate(), reservationForRemoving.getCheckOutDate());
        reservationRepository.deleteById(id);
    }

    @Override
    public Reservation update(Long id, Reservation reservation, String username) {
        var existedReservation = findById(id, username);

        if (!validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                existedReservation.getUser())) {
            throw new AccessDeniedException("Access denied");
        }

        var roomBookedDays = reservation.getRoom().getBookedDates();
        var newBookingDays = roomService.getDateList(reservation.getCheckInDate(), reservation.getCheckOutDate());

        if (!roomService.isAvailableDates(roomBookedDays, newBookingDays)) {
            throw new RoomBookingException("This dates is unavailable");
        }

        roomService.deleteBookedDates(existedReservation.getId(),
                existedReservation.getCheckInDate(),
                existedReservation.getCheckOutDate());

        roomService.addBookedDates(reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate());

        reservation.setId(id);
        reservation.setUser(existedReservation.getUser());

        return reservationRepository.save(reservation);
    }

    @Override
    public String localDateToStr(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        try {
            return date.format(formatter);
        } catch (DateTimeParseException e) {
            throw new RoomBookingException("Input dates is incorrect");
        }
    }
}
