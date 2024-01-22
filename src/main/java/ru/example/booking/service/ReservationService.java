package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.ReservationResponseList;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.mapper.ReservationMapper;
import ru.example.booking.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final RoomService roomService;

    private final UserService userService;

    private final ValidationService validationService;

    private final ReservationMapper reservationMapper;

    @Value("${app.dateFormat}")
    private String datePattern;

    public ReservationResponseList findAll() {
        return reservationMapper.reservationListToResponseList(reservationRepository.findAll(), datePattern);
    }

    public ReservationResponse findById(Long id, String username) {

        var reservation = findReservationById(id);

        validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                reservation.getUser());

        return reservationMapper.reservationToResponse(reservation, datePattern);
    }

    public ReservationResponse booking(UpsertReservationRequest request, String username) {
        var reservation = reservationMapper.requestToReservation(request, datePattern);
        roomService.addBookedDates(reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate());
        reservation.setUser(userService.findByUsernameWithoutPrivilegeValidation(username));
        return reservationMapper.reservationToResponse(reservationRepository.save(reservation), datePattern);
    }

    public void cancel(Long id, String username) {
        Reservation reservationForRemoving = findReservationById(id);

        validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                reservationForRemoving.getUser());

        roomService.deleteBookedDates(id, reservationForRemoving.getCheckInDate(), reservationForRemoving.getCheckOutDate());
        reservationRepository.deleteById(id);
    }

    public ReservationResponse update(Long id, UpsertReservationRequest request, String username) {
        var existedReservation = findReservationById(id);

        var updatedReservation = reservationMapper.requestToReservation(request, datePattern);

        validationService.isValidAction(userService.findByUsernameWithoutPrivilegeValidation(username),
                existedReservation.getUser());

        var roomBookedDays = updatedReservation.getRoom().getBookedDates();
        var newBookingDays = roomService.getDateList(updatedReservation.getCheckInDate(),
                updatedReservation.getCheckOutDate());

        if (!roomService.isAvailableDates(roomBookedDays, newBookingDays)) {
            throw new RoomBookingException("This dates is unavailable");
        }

        roomService.deleteBookedDates(existedReservation.getId(),
                existedReservation.getCheckInDate(),
                existedReservation.getCheckOutDate());

        roomService.addBookedDates(updatedReservation.getRoom().getId(),
                updatedReservation.getCheckInDate(),
                updatedReservation.getCheckOutDate());

        updatedReservation.setId(id);
        updatedReservation.setUser(existedReservation.getUser());

        return reservationMapper.reservationToResponse(reservationRepository.save(updatedReservation), datePattern);
    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation is not found, ID is " + id)
        );
    }

}
